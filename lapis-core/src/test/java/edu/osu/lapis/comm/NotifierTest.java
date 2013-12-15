package edu.osu.lapis.comm;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialization.JsonSerialization;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.ClientCall;
import static edu.osu.lapis.transmission.ClientCall.RestMethod.*;
import edu.osu.lapis.transmission.ClientResponse;
import edu.osu.lapis.transmission.LapisTransmission;
import edu.osu.lapis.util.Sleep;

public class NotifierTest {
	
	private static final List<ClientCall> clientCalls = Collections.synchronizedList(new ArrayList<ClientCall>());
	private static final AtomicReference<Throwable> throwableRef = new AtomicReference<Throwable>();
	
	private final NetworkTable networkTable = new NetworkTable();
	private final Notifier notifier = new Notifier();
	private final LapisSerialization lapisSerialization = new JsonSerialization();
	
	@Before
	public void init() {
		networkTable.setCoordinator(new LapisNode("coord", "url"));
		networkTable.setLocalNode(new LapisNode("localNode", "url.me"));
		networkTable.updateAllNodes(Arrays.asList(
				new LapisNode("one", "http://url.one.com"),
				new LapisNode("two", "http://url.two.net"),
				new LapisNode("three", "http://url.three.org")));
		
		notifier.setNetworkTable(networkTable);
		notifier.setLapisTransmission(new MockLapisTransmission());
		notifier.setLapisSerialization(lapisSerialization);
		
		clientCalls.clear();
		throwableRef.set(null);
	}

	@Test
	public void testNotifyNetworkOfUpdate() {
		LapisNode updated = new LapisNode("one", "http://url.one.UPDATED");
		notifier.notifyNetworkOfUpdate(updated);
		waitForClientCallsToReachSize(2, 50);
		Assert.assertNull(throwableRef.get());
		for(ClientCall call : clientCalls) {
			LapisNode deserialized = lapisSerialization.deserializeLapisNode(call.getPayload());
			Assert.assertEquals(updated.getNodeName(), deserialized.getNodeName());
			Assert.assertEquals(updated.getUrl(), deserialized.getUrl());
			Assert.assertEquals(POST, call.getMethod());
			Assert.assertEquals(updated.getNodeName(), getNodeNameFromUrlPath(call.getUri()));
			String originalNodeUrl = networkTable.getNode(updated.getNodeName()).getUrl();
			Assert.assertFalse(call.getUri().startsWith(originalNodeUrl));
		}
	}
	
	private String getNodeNameFromUrlPath(String url) {
		int lastIndex = url.lastIndexOf('/');
		return url.substring(lastIndex+1);
	}

	@Test
	public void testNotifyNetworkOfNewNode() { 
		LapisNode newNode = new LapisNode("four", "http://somethingOrOther.com:1234");
		notifier.notifyNetworkOfNewNode(newNode);
		waitForClientCallsToReachSize(3, 100);
		Assert.assertNull(throwableRef.get());
		for(ClientCall call : clientCalls) {
			LapisNode deserialized = lapisSerialization.deserializeLapisNode(call.getPayload());
			Assert.assertEquals(newNode.getNodeName(), deserialized.getNodeName());
			Assert.assertEquals(newNode.getUrl(), deserialized.getUrl());
			Assert.assertEquals(PUT, call.getMethod());
			Assert.assertEquals(newNode.getNodeName(), getNodeNameFromUrlPath(call.getUri()));
			Assert.assertFalse(call.getUri().startsWith(newNode.getUrl()));
		}
	}

	@Test
	public void testNotifyNetworkOfDelete() {
		LapisNode deleted = new LapisNode("two", "http://url.two.net");
		notifier.notifyNetworkOfDelete(deleted);
		waitForClientCallsToReachSize(2, 100);
		Assert.assertNull(throwableRef.get());
		for(ClientCall call : clientCalls) {
			Assert.assertEquals(DELETE, call.getMethod());
			Assert.assertNull(call.getPayload());
			Assert.assertEquals(deleted.getNodeName(), getNodeNameFromUrlPath(call.getUri()));
			String originalNodeUrl = networkTable.getNode(deleted.getNodeName()).getUrl();
			Assert.assertFalse(call.getUri().startsWith(originalNodeUrl));
		}
	}
	
	private void waitForClientCallsToReachSize(int size, long millisToWait) {
		final long millis = Math.max(5, millisToWait);
		final long startTime = System.currentTimeMillis();
		while(clientCalls.size() < size) {
			Sleep.sleep(5);
			if(System.currentTimeMillis() > millis + startTime)
				break;
		}
		if(clientCalls.size() < size) {
			throw new IllegalStateException("list never reached size " + size);
		}
	}
	
	@After
	public void reThrowException() throws Throwable {
		Throwable throwable = throwableRef.get();
		if(throwable != null)
			throw throwable;
	}
	
	private static class MockLapisTransmission extends LapisTransmission {

		@Override public synchronized byte[] executeClientCallReturnBytes(ClientCall clientCall) {
			return this.executeClientCall(clientCall).getPayload();
		}

		@Override public synchronized ClientResponse executeClientCall(ClientCall clientCall) {
			Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override public void uncaughtException(Thread t, Throwable e) {
					throwableRef.set(e);
				}
			});
			clientCalls.add(clientCall);
			return null;
		}
	}
}
