package edu.osu.lapis.comm.client;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import edu.osu.lapis.comm.serial.DataClientCommunicationImpl;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;

public class LapisDataClientTest {
	
	@Test
	public void testValidateVariableExistence() {
		final AtomicBoolean underlyingClientCalled = new AtomicBoolean(false);
		
		//test case where variable present
		GlobalDataTable globalDataTable = new GlobalDataTable();
		final NameMetaDataPair pair = getRandomNameMetaDataPair();
		globalDataTable.put(pair.name, pair.meta);
		DataClientCommunicationImpl impl = new DataClientCommunicationImpl() {
			@Override public VariableMetaData getVariableMetaData(VariableFullName varName) {
				underlyingClientCalled.set(true);
				return pair.meta;
			}
		};
		LapisDataClient lapisDataClient = new LapisDataClient();
		lapisDataClient.setDataClientCommunicationImpl(impl);
		lapisDataClient.setGlobalDataTable(globalDataTable);
		VariableFullName varName = new VariableFullName(pair.name);
		lapisDataClient.validateVariableExistence(varName);
		Assert.assertFalse(underlyingClientCalled.get());
		
		//test variable missing
		globalDataTable.remove(varName);
		underlyingClientCalled.set(false);
		lapisDataClient.validateVariableExistence(varName);
		Assert.assertTrue(underlyingClientCalled.get());
	}
	
	private NameMetaDataPair getRandomNameMetaDataPair() {
		VariableMetaData metaData = new VariableMetaData();
		metaData.setLapisPermission(LapisPermission.READ_WRITE);
		String name = RandomStringUtils.randomAlphanumeric(7)
				+ '@' + RandomStringUtils.randomAlphanumeric(8);
		return new NameMetaDataPair(name, metaData);
	}
	
	private static class NameMetaDataPair {
		final String name;
		final VariableMetaData meta;
		
		public NameMetaDataPair(String name, VariableMetaData meta) {
			this.name = name;
			this.meta = meta;
		}
	}
}
