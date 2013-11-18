package edu.osu.lapis.restlets;

import java.util.Arrays;

import org.apache.commons.lang3.Validate;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Filter;

public class LapisFilterChainRestletBase extends LapisRestletBase {

	private Filter[] putFilters, getFilters, postFilters, deleteFilters;
	private Restlet putTargetRestlet, getTargetRestlet, postTargetRestlet, deleteTargetRestlet;
	
	@Override
	public final void put(Request request, Response response) {
		getHandlingRestlet(putFilters, putTargetRestlet).handle(request, response);
	}

	@Override
	public void get(Request request, Response response) {
		getHandlingRestlet(getFilters, getTargetRestlet).handle(request, response);
	}

	@Override
	public final void post(Request request, Response response) {
		getHandlingRestlet(postFilters, postTargetRestlet).handle(request, response);
	}

	@Override
	public final void delete(Request request, Response response) {
		getHandlingRestlet(deleteFilters, deleteTargetRestlet).handle(request, response);
	}
	
	private final Restlet getHandlingRestlet(Filter[] filters, Restlet targetRestlet) {
		nonNullTarget(targetRestlet);
		if(filters == null) {
			return targetRestlet;
		} else {
			return createFilterChainWithTarget(filters, targetRestlet);
		}
	}
	
	private final void nonNullTarget(Restlet targetRestlet) {
		if(targetRestlet == null) {
			throw new IllegalStateException("Target Restlet in Lapis filter chain " 
					+ "was null at the time of call handling.");
		}
	}
	
	/**
	 * Chains the filters together with the target Restlet at the "end" of the
	 * chain. The first Restlet is returned.
	 */
	private Restlet createFilterChainWithTarget(Filter[] filters, Restlet target) {
		Restlet[] restlets = Arrays.copyOf(filters, filters.length + 1, Restlet[].class);
		restlets[filters.length] = target;
		return createRestletFilterChain(restlets);
	}
	
	/**
	 * Chains the Restlets together. All must be Filter instances except the last.
	 * The first Restlet is returned.
	 */
	private Restlet createRestletFilterChain(Restlet ... restlets) { //TODO TEST
		Validate.isTrue(restlets.length > 0, "Must provide at least one Restlet.");
		Filter previousFilter = null;
		Filter currentFilter =  null;
		for(int i = 0; i < restlets.length - 1; ++i) {
			assert restlets[i] instanceof Filter;
			currentFilter = (Filter) restlets[i];
			if(previousFilter != null) {
				previousFilter.setNext(currentFilter);
			}
			previousFilter = currentFilter;
		}
		if(currentFilter != null) {
			currentFilter.setNext(restlets[restlets.length-1]);
		}
		return restlets[0];
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
