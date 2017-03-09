package com.batuhanyaman.huchat.resources;
import java.util.ArrayList;
import java.util.List;

import com.batuhanyaman.huchat.Room;
import com.batuhanyaman.huchat.UserListResponse;
import com.batuhanyaman.huchat.services.AuthService;
import com.batuhanyaman.huchat.services.RoomService;








import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.bson.Document;

@Path("/rooms")
public class RoomResources {

	RoomService roomService = new RoomService();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getRooms(@QueryParam("long") Double longitude,@QueryParam("lat") Double latitude,@QueryParam("id") int userID,@QueryParam("hash") String hash){
		
		AuthService authService = new AuthService();
				
				if(authService.checkAuth(userID, hash))
				{
					Gson gson = new GsonBuilder().create();
					
					return gson.toJson(roomService.getRooms(longitude,latitude));
			
					
				}
				return null;

	}
	
	
	@GET
	@Path("/{roomID}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserListResponse setRoom(@PathParam("roomID") String roomID,@QueryParam("id") int userID,@QueryParam("hash") String hash){
	
		AuthService authService = new AuthService();
		
		if(authService.checkAuth(userID, hash))
		{
			
			return new UserListResponse(roomService.getUsers(roomID));
			
		}
		return null;
		
		
	}
	@POST
	@Path("/{roomID}")
	public void logoutRoom(@PathParam("roomID") String roomID,@QueryParam("id") int userID,@QueryParam("hash") String hash){
	
		AuthService authService = new AuthService();
		
		if(!authService.checkAuth(userID, hash))
			return;
		
		else{
			
			roomService.logoutRoom(roomID);
			
		}
		
	}
	
	
}
