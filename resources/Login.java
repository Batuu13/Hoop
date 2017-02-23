package com.batuhanyaman.huchat.resources;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.batuhanyaman.huchat.LoginResponse;
import com.batuhanyaman.huchat.services.LoginService;

@Path("/login")
public class Login {
	
	private SecureRandom random = new SecureRandom();

	LoginService loginService = new LoginService();
	
	@GET 
	@Produces(MediaType.APPLICATION_JSON)
	public LoginResponse login(@QueryParam("email") String email,@QueryParam("hash") String hashedPass){
		
		
		int userID = loginService.getID(email);
		
		if(userID == -1)
		{
			return  new LoginResponse(2,-1,"null");// email mismatch
		}
		if(loginService.checkActivation(userID))
		{
			return  new LoginResponse(3,-1,"null");// email mismatch
		}
		if(loginService.checkOnline(userID))
		{
			System.out.println("INFO : AYNI ZATEN ONLINE DURUMDAYKEN BAŞKA TELDEN GİRİŞ YAPILDI. ID:" + userID);
			loginService.removeOnline(userID);
			// return new LoginResponse(1,-1,"null"); // User is already logged in
		}
		if(loginService.checkLogin(userID, hashedPass)) // Success
		{
			String newHash = nextSessionId();
			loginService.Login(userID, newHash);
			return new LoginResponse(-1,userID,newHash);
		}
		else
		{
			return new LoginResponse(0,-1,"null"); // email or password don't match
		}
			
	}
		
	public String nextSessionId() 
	{
		    return new BigInteger(130, random).toString(32);
	}
	
	
 	@GET
 	@Path("/auto")
	@Produces(MediaType.TEXT_PLAIN)
	public String AutoLogin(@QueryParam("id") int userID,@QueryParam("hash") String hash)
 	{
		
		System.out.println("Auto Login:" + userID);
		
		if(loginService.checkAuto(userID,hash))
		{
			return "true";
		}
		else
			return "false";
			
	}
}
