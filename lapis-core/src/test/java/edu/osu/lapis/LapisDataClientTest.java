package edu.osu.lapis;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import edu.osu.lapis.communication.DataClientCommunicationImpl;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;

public class LapisDataClientTest {
	
	@Test
	public void testValidateVariableExistenceAndType() {
		final AtomicBoolean underlyingClientCalled = new AtomicBoolean(false);
		
		//test case where variable present
		GlobalDataTable globalDataTable = new GlobalDataTable();
		final NameMetaDataPair pair = getRandomNameMetaDataPair(LapisDataType.DOUBLE);
		globalDataTable.put(pair.name, pair.meta);
		DataClientCommunicationImpl renameMe = new DataClientCommunicationImpl() {
			@Override public VariableMetaData getVariableMetaData(VariableFullName varName) {
				underlyingClientCalled.set(true);
				return pair.meta;
			}
		};
		LapisDataClient lapisDataClient = new LapisDataClient();
		lapisDataClient.setDataClientCommunicationImpl(renameMe);
		lapisDataClient.setGlobalDataTable(globalDataTable);
		VariableFullName varName = new VariableFullName(pair.name);
		lapisDataClient.validateVariableExistenceAndType(varName, pair.meta.getType());
		Assert.assertFalse(underlyingClientCalled.get());
		
		//now test type doesn't match
		underlyingClientCalled.set(false);
		final NameMetaDataPair replacement = getRandomNameMetaDataPair(LapisDataType.INTEGER);
		renameMe = new DataClientCommunicationImpl() {
			@Override public VariableMetaData getVariableMetaData(VariableFullName varName) {
				underlyingClientCalled.set(true);
				return replacement.meta;
			}
		};
		lapisDataClient.setDataClientCommunicationImpl(renameMe);
		try {
			lapisDataClient.validateVariableExistenceAndType(varName, LapisDataType.BOOLEAN);
			Assert.fail();
		} catch (Exception e) {
			//this was expected
		}
		Assert.assertTrue(underlyingClientCalled.get());
		Assert.assertEquals(LapisDataType.INTEGER, globalDataTable.get(varName).getType());
		
		//test variable missing
		globalDataTable.remove(varName);
		underlyingClientCalled.set(false);
		lapisDataClient.validateVariableExistenceAndType(varName, LapisDataType.INTEGER);
		Assert.assertTrue(underlyingClientCalled.get());
	}
	
	private NameMetaDataPair getRandomNameMetaDataPair(LapisDataType type) {
		VariableMetaData metaData = new VariableMetaData();
		metaData.setLapisPermission(LapisPermission.READ_WRITE);
		metaData.setType(type);
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
