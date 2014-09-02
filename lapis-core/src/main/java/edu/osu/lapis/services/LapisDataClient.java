package edu.osu.lapis.services;

import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.exception.LapisClientException;
import edu.osu.lapis.exception.LapisClientExceptionWithStatusCode;
import edu.osu.lapis.util.LapisCache;

import java.util.List;

public class LapisDataClient {

	private final LapisDataClientHelper lapisDataClientHelper;
	private final LapisCache<VariableFullName, VariableMetaData> metaDataCache;

	public LapisDataClient(final LapisDataClientHelper lapisDataClientHelper) {
		this.lapisDataClientHelper = lapisDataClientHelper;
		this.metaDataCache = new LapisCache<VariableFullName, VariableMetaData>(10000 /*TODO MAKE CONFIGURABLE */) {
			@Override
			protected VariableMetaData load(VariableFullName key) {
				try {
					return lapisDataClientHelper.getVariableMetaData(key);
				} catch (LapisClientExceptionWithStatusCode e) {
					if (e.getStatusCode() == 404) {
						return null;
					} else {
						throw e;
					}
				}
			}
		};
	}

	/**
	 * Retrieve the value of the remote variable.
	 *
	 * @param fullName the fully qualified (name@node) name of the published variable.
	 * @param cls      the expected type
	 * @return the value of the remote variable
	 */
	public <T> T getRemoteVariableValue(String fullName, Class<T> cls) {
		VariableFullName variableFullName = new VariableFullName(fullName);
		return lapisDataClientHelper.getVariableValue(variableFullName, cls);
	}

	public VariableMetaData getRemoteVariableMetaData(String fullName) {
		return this.metaDataCache.get(new VariableFullName(fullName));
	}

	public List<VariableMetaData> getVariableMetaDataForNode(String nodeName) {
		return lapisDataClientHelper.getVariableMetaDataForNode(nodeName);
	}

	/**
	 * Set the value of the remote variable.
	 *
	 * @param fullName the fully qualified (name@node) name of the published variable.
	 * @param value    the value to set
	 */
	public void setRemoteVariableValue(String fullName, Object value) {
		VariableFullName variableFullName = new VariableFullName(fullName);
		VariableMetaData meta = getRemoteVariableMetaData(fullName);
		validateNotReadOnly(meta, fullName);
		lapisDataClientHelper.setVariableValue(variableFullName, value);
	}

	private void validateNotReadOnly(VariableMetaData meta, String fullName) {
		if (meta.isReadOnly()) {
			throw new LapisClientException("Attempting to set variable "
					+ fullName + ", which is read-only.");
		}
	}
}
