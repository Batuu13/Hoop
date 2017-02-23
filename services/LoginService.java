package com.batuhanyaman.huchat.services;

import java.util.Date;

import org.bson.Document;

import com.batuhanyaman.huchat.Database;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

public class LoginService {

	private MongoCollection<Document>  memberCollection;
	private MongoDatabase db;
	public LoginService()
	{
		db = Database.getInstance().getMongoClient().getDatabase("test");
		memberCollection =  db.getCollection("members");
		
	}

	public boolean checkLogin(int id,String pass)
	{
		FindIterable<Document> cursor = null;
		try {
			Document query = new Document("id", id);

		cursor = memberCollection.find(query);

		
		   while(cursor.iterator().hasNext()) {
			  Document curObj= cursor.iterator().next();
			  
			   if(curObj.getString("password").equals(pass))
			   {
				   return true;
			   }
		   }
		}
		catch(Exception e)
		{
			return false;
		 }finally {
		   
		cursor.iterator().close();
		}
		return false;
		
	}
	public boolean Login(int id,String hash)
	{
		try{
		MongoCollection<Document> onlineCollection = db.getCollection("online");
		
		
		Document doc = new Document("id", id).
				 	append("hash", hash).
		            append("time", new Date());
		 
		 onlineCollection.insertOne(doc);
		
		 return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public boolean checkOnline(int id)
	{
		MongoCollection<Document> onlineCollection = db.getCollection("online");
		Document query = new Document("id", id);
		
		if(onlineCollection.find(query).first() != null) 
			return true;
		else
			return false;
	}
	public boolean Logout(int id,String hash)
	{
		Document query = new Document("id", id). append("hash", hash);
		
		MongoCollection<Document> onlineCollection = db.getCollection("online");
		
		DeleteResult deleted = onlineCollection.deleteOne(query);
		
		if(deleted.getDeletedCount() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public void removeOnline(int id)
	{
		MongoCollection<Document> onlineCollection = db.getCollection("online");
		
		onlineCollection.deleteOne(new Document("id",id));
		
	}
	public int getID(String email) {
		
		Document query = new Document("mail", email);
		
		MongoCollection<Document> memberCollection = db.getCollection("members");
		FindIterable<Document> member = memberCollection.find(query);
		if(member.first().isEmpty())
			return -1;
		
		return Integer.parseInt(member.first().getInteger("id").toString());
	}

	public boolean checkActivation(int userID) {
		 FindIterable<Document> member = memberCollection.find(new Document("id", userID));
		 if(member.first()!= null)
		 {
			 if(member.first().getString("isActivated").equals("true"))
			 {
				 return false;
			 }
		 }
		return true;
	}

	public boolean checkAuto(int userID, String hash) {
		
		MongoCollection<Document> onlineCollection = db.getCollection("online");
		
		FindIterable<Document> user = onlineCollection.find(new Document("id",userID).append("hash", hash));
		
		if(user.first()!= null)
		{
			return true;
		}
			
		return false;
	}
}
