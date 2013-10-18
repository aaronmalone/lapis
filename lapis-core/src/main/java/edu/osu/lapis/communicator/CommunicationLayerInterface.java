package edu.osu.lapis.communicator;

import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.NetworkTable;

public interface CommunicationLayerInterface {

	//Client
	Object get(VariableFullName fullName, LapisDataType type);
	void set(VariableFullName fullName, Object data);
	
	//Server
	void initialize(LocalDataTable ldt, GlobalDataTable gtd, NetworkTable nt);
	void setLocalDataTable(LocalDataTable ldt);
	void setGlobalDataTable(GlobalDataTable gdt);
	void setNetworkTable(NetworkTable nt);
	void destroy();
	void initialize(LocalDataTable ldt, GlobalDataTable gdt, NetworkTable nt,
			String modelName, String address);
	
}
