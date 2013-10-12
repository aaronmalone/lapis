package edu.osu.lapis.communicator;

import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.NetworkTable;

public interface CommunicationLayerInterface {

	//Client
	public Object get(VariableFullName fullName, LapisDataType type);
	public void set(VariableFullName fullName, Object data);
	
	
	//Server
	public void initialize(LocalDataTable ldt, GlobalDataTable gtd, NetworkTable nt);
	public void setLocalDataTable(LocalDataTable ldt);
	public void setGlobalDataTable(GlobalDataTable gdt);
	public void setNetworkTable(NetworkTable nt);
	public void destroy();
	
}
