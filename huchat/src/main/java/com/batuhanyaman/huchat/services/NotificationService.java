package com.batuhanyaman.huchat.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import com.batuhanyaman.huchat.Database;
import com.batuhanyaman.huchat.util.IDTracker;
import com.batuhanyaman.huchat.util.Notification;
import com.batuhanyaman.huchat.util.NotificationFR;
import com.batuhanyaman.huchat.util.NotificationM;
import com.batuhanyaman.huchat.util.NotificationO;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/*
 * Notification Types:	
 * 	1) Friend Request
 * 	2) Message
 *  3) Other (system messages or announces)
 *  
 *  
 *  Database Design:
 *  
 *  id
 *  type_id (1,2,3)
 *  requester_id (only for 1-2)
 *  reciever_id (only for 1-2)
 *  date
 *  message (only for 3)
 *  isSeen
 *  
*/


public class NotificationService {

	private MongoCollection<Document>  memberCollection;
	private MongoDatabase db;
	private static NotificationService notService = null;
	
	public static NotificationService getInstance()
	{
		if(notService != null)
		{
			return notService;
		}
		return new NotificationService();
	}
	
	
	private NotificationService()
	{
		db = Database.getInstance().getMongoClient().getDatabase("test");
		memberCollection = db.getCollection("members");
	}
	
	@SuppressWarnings("unchecked")
	public List<Document> getNotifications(int userId)
	{
		FindIterable<Document> memberObj = memberCollection.find(new Document("id",userId));
		Document mMember= memberObj.first();
		if((ArrayList<Document>)mMember.get("notifications") == null)
			return new ArrayList<Document>();
		else
			return   (ArrayList<Document>)mMember.get("notifications");
	}
	public void addNotification(int userId, Notification not)
	{
		switch(not.getType_id())
		{
		case 1:
			addFR(userId,(NotificationFR)not);
			break;
		case 2:
			addM(userId,(NotificationM)not);;
			break;
		case 3:
			addO(userId,(NotificationO)not);;
			break;
			default:
				System.out.println("ERROR: UNKOWN NOTIFICATION ID: " + not.getType_id());
		}
		
		
	}

	private void addO(int userId, NotificationO not) {
		memberCollection.findOneAndUpdate(new Document("id", userId),
				new Document("$addToSet",new Document("notifications",
				new Document("id",IDTracker.getNotificationId()) 
					 .append("type_id",not.getType_id())	 
					 .append("date",not.getDate())
					 .append("message",not.getMessage())
					 .append("isSeen",false)
					                    )));
		
	}

	private void addM(int userId, NotificationM not) {
		memberCollection.findOneAndUpdate(new Document("id", userId),
				new Document("$addToSet",new Document("notifications",
				new Document("id",IDTracker.getNotificationId()) 
					 .append("type_id",not.getType_id())
					 .append("requester_id",not.getRequester_id())
					 .append("reciever_id",not.getReciever_id())		 
					 .append("date",not.getDate())
					 .append("isSeen",false)
					                    )));
		
	}

	private void addFR(int userId, NotificationFR not) {
		memberCollection.findOneAndUpdate(new Document("id", userId),
				new Document("$addToSet",new Document("notifications",
				new Document("id",IDTracker.getNotificationId()) 
					 .append("type_id",not.getType_id())
					 .append("requester_id",not.getRequester_id())
					 .append("reciever_id",not.getReciever_id())		 
					 .append("date",not.getDate())
					 .append("isSeen",false)
					                    )));
	}
	
	public int setSeen(int userId,int NotificationId)
	{ 
		try {
			Document query = new Document();
		    query.put("id", userId);
		    query.put("notifications.id", NotificationId);

		    Document data = new Document();
		    data.put("notifications.$.isSeen", true);

		    Document command = new Document();
		    command.put("$set", data);

		    memberCollection.findOneAndUpdate(query, command);
		    return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
			// TODO: handle exception
		}
		
		
	}
	public void deleteNotifications(int userId,int NotificationId)
	{
		System.out.println(userId + " - " + NotificationId);
		 Document query = new Document();
		    query.put("id", userId);
		    query.put("notifications.id", NotificationId);
		    Document update = new Document("$pull", new Document("notifications",new Document("id",NotificationId)));
		    
		    memberCollection.findOneAndUpdate(query, update);
	}
}