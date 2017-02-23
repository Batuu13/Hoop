package com.batuhanyaman.huchat.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.bson.Document;

import com.batuhanyaman.huchat.Message;
import com.batuhanyaman.huchat.services.AuthService;
import com.batuhanyaman.huchat.services.MessageService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/messages")
public class MessageResources {

	MessageService messageService;
	
	
	
	
	
	@GET
	@Path("/{roomID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMessages(@PathParam("roomID") String roomID,@QueryParam("id") int userID,@QueryParam("hash") String hash){
		
		messageService = new MessageService(roomID);
		AuthService authService = new AuthService();
				
				if(!authService.checkAuth(userID, hash))
					return null;
				
		//System.out.println(db.getName());
		return messageService.getMessages(roomID);
	}
	
	@POST
	@Path("/{roomID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessage(Message message,@PathParam("roomID") String roomID,@QueryParam("id") int userID,@QueryParam("hash") String hash){
		//MongoClient mongoClient = new MongoClient();
		//MongoDatabase db = mongoClient.getDatabase("test");
		messageService = new MessageService(roomID);
		
		AuthService auth = new AuthService();
		if(auth.checkAuth(userID, hash)){
			messageService.sendMessage(message);
		}
	}
	
	@GET
	@Path("/inbox/{userID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String getPrivateMessage(@PathParam("userID") int recieverID,@QueryParam("hash") String hash){
		//MongoClient mongoClient = new MongoClient();
		//MongoDatabase db = mongoClient.getDatabase("test");
		messageService = new MessageService();
		
		AuthService auth = new AuthService();
		
		
		if(auth.checkAuth(recieverID, hash)){
			Gson gson = new GsonBuilder().create();
			//TODO ADD MORE
			return gson.toJson(messageService.getPrivateMessages(recieverID));
		}
		return "Auth failed";
	}
	
	
}
