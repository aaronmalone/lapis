package edu.osu.lapis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.osu.lapis.data.LocalDataTable;

/**
 * Java-based Spring context for wiring LAPIS together.
 *
 */
@Configuration public class LapisConfiguration {
	
	@Bean
	public LapisDataClient getLapisClient() {
		LapisDataClient lapisDataClient = new LapisDataClient();
//		lapisDataClient.setGlobalDataTable(new GlobalDataTable());
//		lapisDataClient.setLapisCommunication(getLapisCommunication());
		return lapisDataClient;
	}
	
	@Bean
	public LocalDataTable localDataTable() {
		return new LocalDataTable();
	}

	//candidate for being moved to separate configuration file
//	private ClientCommunication getLapisCommunication() {
//		RestletClientCommunication lapisCommunication = new RestletClientCommunication();
//		lapisCommunication.setLapisSerialization(new JsonSerialization());
//		lapisCommunication.setNetworkTable(new NetworkTable());
//		return lapisCommunication;
//	}
}