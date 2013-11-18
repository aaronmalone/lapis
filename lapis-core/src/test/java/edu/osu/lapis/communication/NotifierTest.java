package edu.osu.lapis.communication;

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
import edu.osu.lapis.transmission.ClientResponse;
import edu.osu.lapis.transmission.LapisTransmission;
import edu.osu.lapis.util.Sleep;

//TODO FINISUH - CURRENTLY UNDER CONSTRUCTIO
public class NotifierTest {
	
	private static final List<ClientCall> clientCalls = Collections.synchronizedList(new ArrayList<ClientCall>());
	private static final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
	
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
		System.out.println("update"); //TODO remove
		LapisNode updated = new LapisNode("one", "http://url.one.new");
		notifier.notifyNetworkOfUpdate(updated);
		waitForClientCallsToReachSize(2, 250);
		Assert.assertEquals(2, clientCalls.size());
		System.out.println("looping through update client calls..."); //TODO REMOVE
		for(ClientCall call : clientCalls) {
			System.out.println(call.getUri()); //TODO REMOVE
			System.out.println(call.getMethod()); //TODO REMOVE
			System.out.println(new String(call.getPayload())); //TODO REMOVE
//			Assert.assertNotEquals(updated.getNodeName(), getNodeNameFromUrlPath(call.getUri()));
		}
	}
	
	private String getNodeNameFromUrlPath(String url) {
		System.out.println("getting node name from url: " + url); //TODO REMOVE
		int lastIndex = url.lastIndexOf('/');
		String nodeName = url.substring(lastIndex+1);
		System.out.println("returning node name: " + nodeName);
		return nodeName;
	}

	@Test
	public void testNotifyNetworkOfNewNode() { 
		//TODO FINISH
	}

	@Test
	public void testNotifyNetworkOfDelete() {
		System.out.println("delete"); //TODO remove
		notifier.notifyNetworkOfDelete(new LapisNode("two", "http://url.two.net"));
		waitForClientCallsToReachSize(2, 250);
		Assert.assertEquals(2, clientCalls.size());
	}
	
	private void waitForClientCallsToReachSize(int size, long millisToWait) {
		final long millis = Math.max(5, millisToWait);
		final long startTime = System.currentTimeMillis();
		while(clientCalls.size() < size) {
			Sleep.sleep(500); //TODO CHANGE
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
