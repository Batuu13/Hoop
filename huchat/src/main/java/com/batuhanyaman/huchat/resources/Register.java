package com.batuhanyaman.huchat.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.batuhanyaman.huchat.Member;
import com.batuhanyaman.huchat.services.MemberService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



@Path("register")
public class Register {


	@POST
	//@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String register( String memberJson){
		Gson gson = new GsonBuilder().create();
		Member newMember = gson.fromJson(memberJson, Member.class);
		MemberService memberService = MemberService.getInstance();
		
		//Logger.getLogger (Register.class.getName()).log(Level.INFO, "hello world");
		System.out.println(memberJson);
		
		newMember.setUniID(memberService.getUniID(newMember.getMail()));
	
		
		if(memberService.IsEmailAvailable(newMember.getMail()))
		{
			if(memberService.RegisterMember(newMember))
			{
				System.out.println("REGISTER : User Regitered: " + newMember.getMail());
				return  "{\"code\": 0}"; // Success
			}
			else
				return   "{\"code\": 1}"; // Something happened on database
		}
		else
		{
			System.out.println("REGISTER : Email missmatch: " + newMember.getMail());
			return   "{\"code\": 2}"; // Email already exists!
		}
		
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String auth(@QueryParam("mail")String mail, @QueryParam("authKey")String authKey)
	{
		MemberService memberService = MemberService.getInstance();
		if(memberService.activate(mail, authKey))
		{
			return "true";
		}
		else{
			return "false"; 
		}
				
	}
		
}
