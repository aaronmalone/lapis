package edu.osu.lapis.communicator;

import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.NetworkTable;

public class CommunicatorLayer implements CommunicationLayerInterface {

	private static LocalDataTable localDataTable;
	private static GlobalDataTable globalDataTable;
	private static NetworkTable networkTable;
	
	@Override
	public Object get(VariableFullName fullName, LapisDataType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(VariableFullName fullName, Object data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(LocalDataTable ldt, GlobalDataTable gdt,
			NetworkTable nt) {

		this.localDataTable = ldt;
		this.globalDataTable = gdt;
		this.networkTable = nt;
		
		
		
		
		

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
