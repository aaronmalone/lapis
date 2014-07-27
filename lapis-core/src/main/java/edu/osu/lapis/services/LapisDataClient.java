package edu.osu.lapis.services;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.osu.lapis.util.NullForMissingCache;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.exception.LapisClientException;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LapisDataClient {

	private final LapisDataClientHelper lapisDataClientHelper;
	private final LoadingCache<VariableFullName, VariableMetaData> metaDataCache;

	public LapisDataClient(final LapisDataClientHelper lapisDataClientHelper) {
		this.lapisDataClientHelper = lapisDataClientHelper;
		LoadingCache<VariableFullName, VariableMetaData> loadingCache = CacheBuilder
				.newBuilder()
				.expireAfterWrite(1, TimeUnit.MINUTES)
				.build(new CacheLoader<VariableFullName, VariableMetaData>() {
					@Override
					public VariableMetaData load(VariableFullName meta) throws Exception {
						return lapisDataClientHelper.getVariableMetaData(meta);
					}
				});
		this.metaDataCache = new NullForMissingCache<VariableFullName, VariableMetaData>(loadingCache);
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
		try {
			return this.metaDataCache.get(new VariableFullName(fullName));
		} catch (ExecutionException e) {
			throw Throwables.propagate(e);
		}
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
