package edu.osu.lapis.communicator;

import java.net.MalformedURLException;
import java.net.URL;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.ServerResource;


import edu.osu.lapis.communicator.restcommunicator.LapisNetworkResource;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;

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
			NetworkTable nt, String nodeName, String nodeAddress) {

		this.localDataTable = ldt;
		this.globalDataTable = gdt;
		this.networkTable = nt;
		
		//Instantiate the server
		this.server = new Component();
        this.server.getServers().add(Protocol.HTTP, 8183); 
		
		
        this.server.getDefaultHost().attach("/network", LapisNetworkResource.class);  
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
		
        LapisNetworkResource n_temp = new LapisNetworkResource();
        
        LapisNode ln_temp = new LapisNode();
        ln_temp.setNodeName(nodeName);
        
        try {
			ln_temp.setUrl(new URL(nodeAddress));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        n_temp.setNodeInformation(ln_temp);
	}

	@Override
	public void initialize(LocalDataTable ldt, GlobalDataTable gtd,
			NetworkTable nt) {
		// TODO Auto-generated method stub
		
	}

}
