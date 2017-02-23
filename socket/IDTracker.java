package socket;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class IDTracker {
	/*
	 * Every function returns a id from database and increase it by 1.
	 * 
	 */
	static MongoDatabase db = Database.getInstance().getMongoClient().getDatabase("test");
	static MongoCollection<Document> counters = db.getCollection("counters");
	public static int getClusterId()
	{
		return getID("cluster");
	}
	public static int getRoomId() {
		return getID("room");
	}
	public static int getNotificationId() {
		return getID("notification");
	}
	private static int getID(String name)
	{
		Document filter = new Document("_id",name + "ID"); 
		Document counter = counters.find(filter).first();
		if(counter == null)
		{
			counters.insertOne(new Document("_id",0));
			return 0;
		}
		else
		{
			int count = counter.getInteger("count");
			Document update = new Document("$inc",new Document("count",1));
			counters.updateOne(filter, update);
			return count;
		}
	}
	public static Object getMessageID() {
		return getID("message");
		
	}
	
	
	
}
