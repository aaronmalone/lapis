package edu.osu.lapis.communicator.restcommunicator;



import org.restlet.Request;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class NetworkResource extends ServerResource{

	@Get (value = "json")
	public String reportNameAndAddress(){
		return null;
		
		
	}
	
	@Put
	public Representation createNetworkTableEntry(Representation re){
		return re;
		
		
		
	}
	
	@Post
	public Representation updateNetworkTableEntry(Representation re){
		return re;
		
		
		
	}
	
	@Delete
	public void deleteNetworkTableEntry(){
		
	}
	
	

}
