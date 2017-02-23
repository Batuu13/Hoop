package com.batuhanyaman.huchat.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.batuhanyaman.huchat.Message;
import com.batuhanyaman.huchat.services.AuthService;
import com.batuhanyaman.huchat.services.MemberService;
import com.batuhanyaman.huchat.services.MessageService;

@Path("/members")
public class MemberResources {

	MemberService memberService;
	
	@GET
	@Path("/{memberID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMember(@PathParam("memberID") int memberID,@QueryParam("id") int id,@QueryParam("hash") String hash){
		
		AuthService authService = new AuthService();
		
		if(!authService.checkAuth(id, hash))
			return null;
		memberService = MemberService.getInstance();
		
		//System.out.println(db.getName());
		return memberService.getMember(memberID);
	}
	@GET
	@Path("/name/{memberID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMemberNameOnly(@PathParam("memberID") int memberID,@QueryParam("id") int id,@QueryParam("hash") String hash){
		
		AuthService authService = new AuthService();
		
		if(!authService.checkAuth(id, hash))
			return null;
		memberService = MemberService.getInstance();
		
		//System.out.println(db.getName());
		return memberService.getName(memberID);
	}
	
	@PUT
	@Path("/profile/{memberID}")
	public String updateMember(@FormParam("name") String name,@FormParam("image") String image,@PathParam("memberID") int memberID,@QueryParam("hash") String hash)
	{
	AuthService authService = new AuthService();
		
		if(!authService.checkAuth(memberID, hash))
			return null;
		memberService = MemberService.getInstance();
		
		
		return memberService.updateMember(memberID,name,image);
	}
	
	@GET
	@Path("/profile/{memberID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getImage(@PathParam("memberID") int memberID,@QueryParam("id") int requesterID,@QueryParam("hash") String hash){
		
		AuthService authService = new AuthService();
		
		if(!authService.checkAuth(requesterID, hash))
			return null;
		memberService = MemberService.getInstance();
		
		//System.out.println(db.getName());
		return memberService.getImage(memberID);
	}
	
	@GET
	@Path("/friend/request/{userID}")
	public int sendRequest(@PathParam("userID") int receiverID,@QueryParam("id") int userID,@QueryParam("hash") String hash){
		//MongoClient mongoClient = new MongoClient();
		//MongoDatabase db = mongoClient.getDatabase("test");
		memberService = MemberService.getInstance();
		
		AuthService auth = new AuthService();
		if(auth.checkAuth(userID, hash)){
			return memberService.sendFriendRequest(receiverID,userID);
		}
		return 0; // Auth Failed
	}
	/*
	 * Friend is getting added.
	 */
	@GET
	@Path("/friend/accept/{requestID}")
	public int acceptRequest(@PathParam("requestID") int requestID,@QueryParam("notID") int notID,@QueryParam("id") int userID,@QueryParam("hash") String hash){
		//MongoClient mongoClient = new MongoClient();
		//MongoDatabase db = mongoClient.getDatabase("test");
		memberService = MemberService.getInstance();
		
		AuthService auth = new AuthService();
		if(auth.checkAuth(userID, hash)){
			return memberService.acceptFriendRequest(requestID,userID,notID);
		}
		return 0; // Auth Failed
	}
	/*
	 * Friend request is rejected and deleted from "requests" queue.
	 */
	@GET
	@Path("/friend/reject/{requestID}")
	public int rejectRequest(@PathParam("requestID") int requestID,@QueryParam("notID") int notID,@QueryParam("id") int userID,@QueryParam("hash") String hash){
		//MongoClient mongoClient = new MongoClient();
		//MongoDatabase db = mongoClient.getDatabase("test");
		memberService = MemberService.getInstance();
		
		AuthService auth = new AuthService();
		if(auth.checkAuth(userID, hash)){
			return memberService.rejectFriendRequest(requestID,userID,notID);
		}
		return 0; // Auth Failed
	}
	/*
	 * Friend request is rejected and deleted from "requests" queue.
	 */
	@GET
	@Path("/friend/delete/{friendID}")
	public int deleteFriend(@PathParam("friendID") int friendID,@QueryParam("id") int userID,@QueryParam("hash") String hash){
		//MongoClient mongoClient = new MongoClient();
		//MongoDatabase db = mongoClient.getDatabase("test");
		memberService = MemberService.getInstance();
		
		AuthService auth = new AuthService();
		if(auth.checkAuth(userID, hash)){
			return memberService.deleteFriend(friendID,userID);
		}
		return 0; // Auth Failed
	}
	@GET
	@Path("/friend/{id}")
	public String getFriendList(@PathParam("id") int id,@QueryParam("id") int userID,@QueryParam("hash") String hash){
		//MongoClient mongoClient = new MongoClient();
		//MongoDatabase db = mongoClient.getDatabase("test");
		memberService = MemberService.getInstance();
		System.out.println(id + " " + hash);
		AuthService auth = new AuthService();
		if(auth.checkAuth(userID, hash)){
			return memberService.getFriends(id);
		}
		System.out.println("ERROR AUTH FAILED");
		return ""; // Auth Failed
	}
	
}


