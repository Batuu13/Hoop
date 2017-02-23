package com.batuhanyaman.huchat.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.bson.Document;

import com.batuhanyaman.huchat.services.AuthService;
import com.batuhanyaman.huchat.services.MemberService;
import com.batuhanyaman.huchat.services.NotificationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
@Path("/notifications")
public class NotificationResources {

	NotificationService notificationService = NotificationService.getInstance();
	
	
	@GET
	public String getNotifications(@QueryParam("id") int userID,@QueryParam("hash") String hash){
		//MongoClient mongoClient = new MongoClient();
		//MongoDatabase db = mongoClient.getDatabase("test");
		
		
		AuthService auth = new AuthService();
		if(auth.checkAuth(userID, hash)){
			  GsonBuilder builder = new GsonBuilder();
              Gson gson = builder.create();
			List<Document> notifications = notificationService.getNotifications(userID);
			
			return gson.toJson(notifications);
		}
		return ""; // Auth Failed
	}
	@GET
	@Path("/{notID}")
	public int setSeen(@PathParam("notID") int notID,@QueryParam("id") int userID,@QueryParam("hash") String hash){
		//MongoClient mongoClient = new MongoClient();
		//MongoDatabase db = mongoClient.getDatabase("test");
		
		
		AuthService auth = new AuthService();
		if(auth.checkAuth(userID, hash)){
			
		
			
			return notificationService.setSeen(userID, notID);
		}
		return 0; // Auth Failed
	}
	
}
