package com.batuhanyaman.huchat.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.batuhanyaman.huchat.services.LoginService;

@Path("/logout")
public class Logout {

	LoginService loginService = new LoginService();
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String logout(@QueryParam("id") int userID,@QueryParam("hash") String hash){
		
		if(loginService.Logout(userID, hash))
		{
			return "Logout Successful";
		}
		else
			return "error";
			
	}
}
