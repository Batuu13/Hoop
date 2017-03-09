package com.batuhanyaman.huchat;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

@XmlRootElement
public class LoginResponse {
	
	private int errorID;
	private int ID;
	private String hash;
	private Member member;
	
	
	public LoginResponse(){}
	public LoginResponse(int errorID, int ID,String hash)
	{
		super();
		this.errorID = errorID;
		this.ID = ID;
		this.hash = hash;
		
		
		MongoDatabase db = Database.getInstance().getMongoClient().getDatabase("test");
		FindIterable<Document> find = db.getCollection("members").find(new Document("id",ID));
		if(find.iterator().hasNext())
		{
			Gson gson = new GsonBuilder().create();
			this.member = gson.fromJson(find.iterator().next().toJson(), Member.class);
			System.out.println(member);
		}
		
	}
	@XmlElement
	public int getErrorID() {
		return errorID;
	}
	@XmlElement
	public int getID() {
		return ID;
	}
	@XmlElement
	public String getHash() {
		return hash;
	}
	@XmlElement
	public Member getMember() {
		return member;
	}
}
