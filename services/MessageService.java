package com.batuhanyaman.huchat.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bson.Document;

import com.batuhanyaman.huchat.Database;
import com.batuhanyaman.huchat.Date;
import com.batuhanyaman.huchat.Message;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MessageService {
private MongoCollection<Document>  roomCollection;
private static final String ROOM_PREFIX = "room_";	
private MongoDatabase db;
private String roomName;
	public MessageService(String roomID)
	{
		
		db = Database.getInstance().getMongoClient().getDatabase("test");
		roomCollection = db.getCollection(ROOM_PREFIX + roomID);
		roomName = roomID;
		
	}
	public MessageService()
	{
		
		db = Database.getInstance().getMongoClient().getDatabase("test");
		
		
	}
	public void sendMessage(Message message)
	{
		MongoCollection<Document> counterCollection = db.getCollection("counters");
		
		FindIterable<Document> counter = counterCollection.find(new Document("_id",roomName));
		
		 Document doc = new Document("messageID", counter.first().get("count")).
				 	append("writerID", message.getWriterID()).
		            append("writerName", message.getWriterName()).
		            append("writerGender", message.getWriterGender()).
		            append("message", message.getMessage()).
		            append("time",  Date.getDate());
		roomCollection.insertOne(doc);
		int finalCount = (int)counter.first().get("count") + 1;
		counterCollection.findOneAndUpdate(new Document("_id",roomName), new Document("count",finalCount));
		/*	
		DBObject header = roomCollection.findOne(new BasicDBObject("id", 0)); // Header
		BasicDBList  as = (BasicDBList) header.get("online");
		
		DBCollection memberCollection = (DBCollection) db.getCollection("members");
		
		DBObject member = null;
		
		for(int i = 0; i < as.size();i++)
		{
			int index = (int)Float.parseFloat((as.get(i).toString()));
			
			member = memberCollection.findOne(new BasicDBObject("id",index));
			
			
			memberCollection.update(member, new BasicDBObject("$push", new BasicDBObject("messagesToGet", doc)));
		}
		
		*/
		
		//memberCollection.
	}

	public String getMessages(String roomID)
	{
		
    	
    	ArrayList<Document> list  =  roomCollection.find().sort( new BasicDBObject("_id", -1)).limit(25).into(new ArrayList<Document>());
    				
		return list.toString();
	}
	
	/* getPrivateMessages(int recieverID)
	 * This method will return the last messages of all of the inbox. 
	 */
	public ArrayList<Document> getPrivateMessages(int recieverID) {
		
		MongoCollection<Document> inboxCollection =  db.getCollection("inbox");
		
		FindIterable<Document> user = inboxCollection.find(new Document("receiverID",recieverID));
		
		if(user.first() != null)
		{
			@SuppressWarnings("unchecked")
			ArrayList<Document> inbox = (ArrayList<Document>) user.first().get("inbox");
			ArrayList<Document> returnMessages =  new ArrayList<>();
			for(int i = 0 ; i < inbox.size() ; i++) // getting the last message of every person in inbox
			{
				Document inboxMessage = new Document();
				
				@SuppressWarnings("unchecked")
				ArrayList<Document> tmpInbox =  (ArrayList<Document>) inbox.get(i).get("messages");
				
				int id = inbox.get(i).getInteger("senderId");
				String name = MemberService.getInstance().getName(id);
				String message = tmpInbox.get(tmpInbox.size() - 1).getString("message");
				String time = tmpInbox.get(tmpInbox.size() - 1).getString("time");
						
				inboxMessage.put("name",name);
				inboxMessage.put("id", id);
				inboxMessage.put("message", message);
				inboxMessage.put("time", time);
				
				returnMessages.add(inboxMessage);
			}
			return returnMessages;
		}
		else
		{
			return new ArrayList<Document>();
		}
	}
	
	
}
