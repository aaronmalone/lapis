package edu.osu.lapis.communicator.rest;

import org.restlet.Component;
import org.restlet.data.Protocol;

import edu.osu.lapis.communicator.CommunicationLayerInterface;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialize.JsonSerialization;

public class RESTCommunicatorLayer implements CommunicationLayerInterface {

	private static LocalDataTable localDataTable;
	private static GlobalDataTable globalDataTable;
	private static NetworkTable networkTable;
	
	private Component server;
	private LapisNode node;
	
	
	@Override
	public Object get(VariableFullName fullName, LapisDataType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(VariableFullName fullName, Object data) {
		// TODO Auto-generated method stub

	}

	//TODO: Implement properties file for port number and address
	//TODO: Implement other routes besides the NetworkRoute
	@Override
	public void initialize(LocalDataTable ldt, GlobalDataTable gdt,
			final NetworkTable nt, String nodeName, String nodeAddress) {

		this.localDataTable = ldt;
		this.globalDataTable = gdt;
		this.networkTable = nt;
		
		//Instantiate the server
		this.server = new Component();
        this.server.getServers().add(Protocol.HTTP, 8183);
        
        /*
        Restlet myRestlet = new Restlet() {
        	
        	final NetworkTable networkTable = nt;

			@Override
			public void handle(Request request, Response response) {
				Method meth = request.getMethod();
				if(meth.equals(Method.GET)) {
					get(request, response);
				} else if(meth.equals(Method.PUT)) {
					put(request, response);
				}
			}

			private void put(Request request, Response response) {
				try {
					String stuff = CharStreams.toString(request.getEntity().getReader());
					LapisNode lapisNode = new LapisNode();
					lapisNode.setNodeName(stuff);
					lapisNode.setUrl(null);
					networkTable.addNode(lapisNode);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			private void get(Request request, Response response) {
				String json = new Gson().toJson(networkTable.getNodesList());
				response.setEntity("aaronWas...<br/>" + json, MediaType.TEXT_PLAIN);
			}
        	
        	
//        	
//			@Override
//			public void handle(Request request, Response response) {
//				String serialized = new Gson().toJson(lapisNode);
//				response.setEntity(serialized, MediaType.APPLICATION_JSON);
//			}
        	
//        	@Get
//        	public Representation someRandomMethod(Request request, Response response) {
//        		response.setEntity("aaronWasHere", MediaType.TEXT_PLAIN);
//        		return response.getEntity();
//        	}
        };
        LapisNetworkResource lapisNetworkResource = new LapisNetworkResource();
        LapisNode lapisNode = new LapisNode();
		lapisNode.setNodeName("me");
		lapisNode.setUrl("http://my.url");
		
        lapisNetworkResource.setNode(lapisNode);
        lapisNetworkResource.setLs(new JsonSerialization());
        lapisNetworkResource.setNetworkTable(new NetworkTable());
        this.server.getDefaultHost().attach("/network", lapisNetworkResource);
         */
//        Router router = new Router(server.getContext());
//        router.att
        
        //server.getDefaultHost().attach("/model/{...}", ...); 
        //server.getDefaultHost().attach("/variable/{...}", ...);
        //server.getDefaultHost().attach("/heartbeat/{...}", ...);

        
        this.initializeNetworkRoute(nodeName, nodeAddress);
        
        
        //Start the server
        try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	

	}

	@Override
	public void setLocalDataTable(LocalDataTable ldt) {
		this.localDataTable = ldt;

	}

	@Override
	public void setGlobalDataTable(GlobalDataTable gdt) {
		this.globalDataTable = gdt;

	}

	@Override
	public void setNetworkTable(NetworkTable nt) {
		this.networkTable = nt;

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	 //Set up the Node information in the Network Route (initialize the static information for GET operations)
	private void initializeNetworkRoute(String nodeName, String nodeAddress){
//		
//        LapisNetworkResource n_temp = new LapisNetworkResource();
//        
//        LapisNode ln_temp = new LapisNode();
//        ln_temp.setNodeName(nodeName);
//        
//        ln_temp.setUrl(nodeAddress);
//        n_temp.setNodeInformation(ln_temp);
	}

	@Override
	public void initialize(LocalDataTable ldt, GlobalDataTable gtd,
			NetworkTable nt) {
		// TODO Auto-generated method stub
		
	}
}
