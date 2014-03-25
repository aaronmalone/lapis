package edu.osu.lapis.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import com.google.common.collect.Lists;

public class NetworkChangeHandler implements NetworkChangeCallback {
	
	private static enum ChangeType { ADD, DELETE }
	
	private final List<NetworkChangeCallback> callbacks = 
			Collections.synchronizedList(new ArrayList<NetworkChangeCallback>());
	
	private final Executor executor;
	
	public NetworkChangeHandler(Executor executor) {
		this.executor = executor;
	}
	
	public void addCallback(NetworkChangeCallback callback) {
		callbacks.add(callback);
	}

	@Override
	public void onNodeAdd(LapisNode lapisNode) {
		dispatchToCallbacks(ChangeType.ADD, lapisNode);
	}

	@Override
	public void onNodeDelete(LapisNode lapisNode) {
		dispatchToCallbacks(ChangeType.DELETE, lapisNode);	
	}
	
	private void dispatchToCallbacks(final ChangeType type, final LapisNode lapisNode) {
		List<Runnable> runnables = Lists.newArrayList();
		synchronized(callbacks) {
			for(final NetworkChangeCallback callback : callbacks) {
				runnables.add(new Runnable() {
					@Override public void run() {
						if(type == ChangeType.ADD) {
							callback.onNodeAdd(lapisNode);
						} else {
							assert(type == ChangeType.DELETE);
							callback.onNodeDelete(lapisNode);
						}
					}
				});
			}
		}
		for(Runnable r : runnables) {
			executor.execute(r);
		}
	}
}
