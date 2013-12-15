package edu.osu.lapis.transmission;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Protocol;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

import edu.osu.lapis.comm.client.LapisNetworkClient;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.util.LapisRestletUtils;

public class LapisDataTransmissionTest {
	
	private LapisDataTransmission lapisDataTransmission;
	private Representation responseEntity;
	
	@Before
	public void setUpResponseEntity() {
		String entity = "RESPONSE ENTITY";
		ByteArrayInputStream inputStream = new ByteArrayInputStream(entity.getBytes());
		responseEntity = new InputRepresentation(inputStream);
	}
	
	@Before
	public void setUpLapisDataTransmission() {
		lapisDataTransmission = new LapisDataTransmission();
		lapisDataTransmission.setLapisNetworkClient(getLapisNetworkClient());
		lapisDataTransmission.setVariableMetaDataPath("metadata");
		lapisDataTransmission.setVariableValuePath("model");
		lapisDataTransmission.setLapisTransmission(new LapisTransmission());
	}
	
	@Before
	public void setUpRestletContext() {
		//this context stuff allows up to handle the client call
		Context context = new Context();
		context.setClientDispatcher(new Client(Protocol.HTTP) {
			@Override public void handle(Request request, Response response) {
				Assert.assertNull(request.getEntity());
				response.setEntity(responseEntity);
			}
		});
		Context.setCurrent(context);
	}
	
	private LapisNetworkClient getLapisNetworkClient() {
		return new LapisNetworkClient() {
			@Override public LapisNode getLapisNode(String nodeName) {
				return new LapisNode(nodeName, RandomStringUtils.randomAlphanumeric(16));
			}
		};
	}
	
	//TODO RE-ORDER MEMBERS
	
	@Test
	public void testSetVariableValue() {
		final byte[] randomBytes = RandomStringUtils.random(100).getBytes();
		Context.getCurrent().setClientDispatcher(new Client(Protocol.HTTP){
			@Override public void handle(Request request, Response response) {
				Assert.assertTrue(Arrays.equals(randomBytes, LapisRestletUtils.getMessageEntityAsBytes(request)));
			}
		});
		VariableFullName varName = getVariableFullNameWithRandomData();
		lapisDataTransmission.setVariableValue(varName, randomBytes);
	}
	
	@Test 
	public void testGetVariableMetaData() {
		VariableFullName varName = getVariableFullNameWithRandomData();
		byte[] bytes = lapisDataTransmission.getVariableMetaData(varName);
		String response = new String(bytes);
		Assert.assertEquals("RESPONSE ENTITY", response);
	}
	
	@Test
	public void testGetVariableValue() throws IOException {
		VariableFullName varName = getVariableFullNameWithRandomData();
		byte[] bytes = lapisDataTransmission.getVariableValue(varName);
		String response = new String(bytes);
		Assert.assertEquals("RESPONSE ENTITY", response);
	}
	
	private VariableFullName getVariableFullNameWithRandomData() {
		return new VariableFullName(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10));
	}
}
