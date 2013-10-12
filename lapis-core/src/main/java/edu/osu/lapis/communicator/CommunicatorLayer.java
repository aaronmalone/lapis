package edu.osu.lapis.communicator;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.ServerResource;


import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.NetworkTable;

public class CommunicatorLayer implements CommunicationLayerInterface {

	private static LocalDataTable localDataTable;
	private static GlobalDataTable globalDataTable;
	private static NetworkTable networkTable;
	
	private Component server;
	
	
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
	@Override
	public void initialize(LocalDataTable ldt, GlobalDataTable gdt,
			NetworkTable nt) {

		this.localDataTable = ldt;
		this.globalDataTable = gdt;
		this.networkTable = nt;
		
		//Instantiate the server
		this.server = new Component();
        this.server.getServers().add(Protocol.HTTP, 8183); 
		
		
        server.getDefaultHost().attach("/network", NetworkTable.class);  
        //server.getDefaultHost().attach("/user/{user}", User.class); 
        //server.getDefaultHost().attach("/variables/{varname}", ModelDataCommunicator.class);
		
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

}
