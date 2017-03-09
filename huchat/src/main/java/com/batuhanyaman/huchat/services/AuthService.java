package com.batuhanyaman.huchat.services;

import org.bson.Document;

import com.batuhanyaman.huchat.Database;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class AuthService {

	MongoCollection<Document> onlineCollection;
	public AuthService(){
		
		
		MongoDatabase db = Database.getInstance().getMongoClient().getDatabase("test");
		onlineCollection =  db.getCollection("online");
	}
	
	public boolean checkAuth(int id, String hash)
	{
		
		Document query = new Document("id", id).append("hash", hash);
		return true;
/*
		if(onlineCollection.find(query).first() != null ) 
			return true;
		else
			return false;
			*/
	
	}
	
}
