package edu.osu.lapis.communicator.rest;

import java.util.Arrays;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Filter;

public class LapisFilterChainRestletBase extends LapisRestletBase { //TODO RENAME

	private Filter[] putFilters, getFilters, postFilters, deleteFilters;
	private Restlet putTargetRestlet, getTargetRestlet, postTargetRestlet, deleteTargetRestlet;
	
	@Override
	public final void put(Request request, Response response) {
		getHandlingRestlet(putFilters, putTargetRestlet);
	}

	@Override
	public void get(Request request, Response response) {
		getHandlingRestlet(getFilters, getTargetRestlet);
	}

	@Override
	public final void post(Request request, Response response) {
		getHandlingRestlet(postFilters, postTargetRestlet);
	}

	@Override
	public final void delete(Request request, Response response) {
		getHandlingRestlet(deleteFilters, deleteTargetRestlet);
	}
	
	private final Restlet getHandlingRestlet(Filter[] filters, Restlet targetRestlet) {
		nonNullTarget(targetRestlet);
		if(filters == null) {
			return targetRestlet;
		} else {
			return createFilterChain(filters, targetRestlet);
		}
	}
	
	private final void nonNullTarget(Restlet targetRestlet) {
		if(targetRestlet == null) {
			throw new IllegalStateException("Target Restlet in Lapis filter chain " 
					+ "was null at the time of call handling.");
		}
	}
	
	private Restlet createFilterChain(Filter[] filters, Restlet target) {
		Restlet[] restlets = Arrays.copyOf(filters, filters.length + 1, Restlet[].class);
		restlets[filters.length] = target;
		return NetworkRestletUtils.createRestletFilterChain(restlets);
	}
	
	public void setPutFilters(Filter ... putFilters) {
		this.putFilters = putFilters;
	}
	
	public void setGetFilters(Filter ... getFilters) {
		this.getFilters = getFilters;
	}
	
	public void setPostFilters(Filter ... postFilters) {
		this.postFilters = postFilters;
	}
	
	public void setDeleteFilters(Filter ... deleteFilters) {
		this.deleteFilters = deleteFilters;
	}
	
	public void setPutTargetRestlet(Restlet putTargetRestlet) {
		this.putTargetRestlet = putTargetRestlet;
	}
	
	public void setGetTargetRestlet(Restlet getTargetRestlet) {
		this.getTargetRestlet = getTargetRestlet;
	}
	
	public void setPostTargetRestlet(Restlet postTargetRestlet) {
		this.postTargetRestlet = postTargetRestlet;
	}
	
	public void setDeleteTargetRestlet(Restlet deleteTargetRestlet) {
		this.deleteTargetRestlet = deleteTargetRestlet;
	}
}
