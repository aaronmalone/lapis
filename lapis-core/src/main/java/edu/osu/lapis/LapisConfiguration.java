package edu.osu.lapis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.osu.lapis.communicator.ClientCommunication;
import edu.osu.lapis.communicator.LapisClient;
import edu.osu.lapis.communicator.rest.ClientCommunicationRestImpl;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialize.JsonSerialization;

/**
 * Java-based Spring context for wiring LAPIS together.
 *
 */
@Configuration public class LapisConfiguration {
	
	@Bean
	public LapisClient getLapisClient() {
		LapisClient lapisClient = new LapisClient();
		lapisClient.setGlobalDataTable(new GlobalDataTable());
		lapisClient.setLapisCommunication(getLapisCommunication());
		return lapisClient;
	}
	
	@Bean
	public LocalDataTable localDataTable() {
		return new LocalDataTable();
	}

	//candidate for being moved to separate configuration file
	private ClientCommunication getLapisCommunication() {
		ClientCommunicationRestImpl lapisCommunication = new ClientCommunicationRestImpl();
		lapisCommunication.setLapisSerialization(new JsonSerialization());
		lapisCommunication.setNetworkTable(new NetworkTable());
		return lapisCommunication;
	}
}