package edu.osu.lapis.network;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class NetworkChangeHandler implements NetworkChangeCallback {

	private final CopyOnWriteArrayList<NetworkChangeCallback> callbacks =
			new CopyOnWriteArrayList<NetworkChangeCallback>();

	private final Executor executor;

	public NetworkChangeHandler(Executor executor) {
		this.executor = executor;
	}

	public void addCallback(NetworkChangeCallback callback) {
		callbacks.add(callback);
	}

	@Override
	public void onNodeAdd(LapisNode lapisNode) {
		CreateRunnableBiFunction function = new CreateRunnableBiFunction() {
			@Override
			public Runnable apply(final NetworkChangeCallback callback, final LapisNode node) {
				return new Runnable() {
					@Override
					public void run() {
						callback.onNodeAdd(node);
					}
				};
			}
		};
		passToCallbacks(function, lapisNode);
	}

	@Override
	public void onNodeDelete(LapisNode lapisNode) {
		CreateRunnableBiFunction function = new CreateRunnableBiFunction() {
			@Override
			public Runnable apply(final NetworkChangeCallback callback, final LapisNode node) {
				return new Runnable() {
					@Override
					public void run() {
						callback.onNodeDelete(node);
					}
				};
			}
		};
		passToCallbacks(function, lapisNode);
	}

	private void passToCallbacks(CreateRunnableBiFunction createRunnableBiFunction, LapisNode lapisNode) {
		for (NetworkChangeCallback callback : callbacks) {
			Runnable runnable = createRunnableBiFunction.apply(callback, lapisNode);
			executor.execute(runnable);
		}
	}

	private static interface CreateRunnableBiFunction {
		Runnable apply(NetworkChangeCallback callback, LapisNode node);
	}
}
