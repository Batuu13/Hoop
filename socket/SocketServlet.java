package socket;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@ServerEndpoint(value = "/messages/{roomID}")
public class SocketServlet{
    //notice:not thread-safe
	private static final String ROOM_PREFIX = "room_";	
	static ArrayList<Session>  sessionList = new ArrayList<>(); //TODO CHANGE TO HASHMAP
    MongoClient client = Database.getInstance().getMongoClient();
    
    MongoDatabase DB = client.getDatabase("test");
    @OnOpen 
    public void onOpen(@PathParam("roomID") String roomID, Session session) throws IOException{
    	try{
    	Map<String,String> queryParams = ParseQuery(session.getQueryString());
    	 int clusterID = Integer.parseInt(queryParams.get("clusterID"));
    	 session.getUserProperties().put("roomID",roomID);
    	 session.getUserProperties().put("clusterID",clusterID);
    	 session.getUserProperties().put("userID",queryParams.get("id"));
    	 sessionList.add(session);
    	
    	
    	 
    	 MongoCollection<Document> clusterCollection = DB.getCollection("clusters");
    	 //increase count
    	 Document filter = new Document();
    	 filter.put("_id", clusterID);
    	 filter.put("points._id", Integer.parseInt(roomID));
    	
    	 
    	 Document userValue = new Document();
    	 userValue.put("onlineMembers" , queryParams.get("id").toString());
    	 
    	 Document update = new Document();
    	 update.put("$addToSet", new Document("points.$.onlineMembers",userValue));
    	 
    	 clusterCollection.findOneAndUpdate(filter,update);
 		//get room
 		FindIterable<Document> room =  clusterCollection.find(filter);
 		
    	//Send online count
    	@SuppressWarnings("unchecked")
		int onlineCount = 5;
    	for(Session user : sessionList)
        {
        	         
        	if(user.getUserProperties().get("roomID").equals(roomID))
        		{
        		
        		user.getBasicRemote().sendText("online:" + onlineCount);
        		}
        }
    	 // 
    	}catch (Exception e) {
    		e.printStackTrace();
		}
        
    		try {
    			
    				
    			ArrayList<Document> list = getLastMessages(roomID);
    			for(int i = list.size() -1; i >= 0; i--)
    			{
    				session.getBasicRemote().sendText(list.get(i).toJson());
    			}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
           
            //asynchronous communication
         
           
       
    }
    
    private Map<String, String> ParseQuery(String queryString) {
		String[] initial = queryString.split("&");
		Map<String,String> params = new HashMap<String,String>();
    	for (String param : initial) {
    		String[] values = param.split("=");
    		params.put(values[0],values[1]);
		}
		
		return params;
	}
    // Returns last 25 messages of that room
	private ArrayList<Document> getLastMessages(String roomID) {
    	MongoCollection<Document> roomCollection = DB.getCollection(ROOM_PREFIX + roomID);
    	ArrayList<Document> list  =  roomCollection.find().sort( new BasicDBObject("_id", -1)).limit(25).into(new ArrayList<Document>());
    				
		return list;
	}

	@OnClose
    public void onClose(Session session){
		Map<String, Object> properties = session.getUserProperties();
		int clusterID = Integer.parseInt(properties.get("clusterID").toString());
		
    	 MongoCollection<Document> clusterCollection = DB.getCollection("clusters");
		 Document filter = new Document();
    	 filter.put("_id", clusterID);
    	 filter.put("points._id", Integer.parseInt(properties.get("roomID").toString()));
    	 
    	 Document userValue = new Document();
    	 userValue.put("onlineMembers" , properties.get("userID").toString());
    	 
    	 Document update = new Document();
    	 update.put("$pull", new Document("points.$.onlineMembers",userValue));
    	 
    	 clusterCollection.findOneAndUpdate(filter,update);
    	 sessionList.remove(session);
    }
    @OnError
    public void onError(Throwable t) {
       t.printStackTrace();
       t.getLocalizedMessage();
    }
    @OnMessage
    public void onMessage(@PathParam("roomID") String roomID ,String message){
    	
    	
    	
    	
    	Gson gson = new Gson();
    	MongoCollection<Document> roomCollection = DB.getCollection(ROOM_PREFIX + roomID);
    	// convert java object to JSON format,
    	// and returned as JSON formatted string
    	 JsonParser parser = new JsonParser();
    	  com.google.gson.JsonObject obj = parser.parse(message).getAsJsonObject();
    	
    	Message messageObj = gson.fromJson(obj,Message.class);
    	
    
		String time = String.format("%d.%d.%d_%02d:%02d",Calendar.getInstance().get(Calendar.DAY_OF_MONTH),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
		 Document doc = new Document("messageID", IDTracker.getMessageID()).
				 	append("writerID", messageObj.getWriterID()).
		            append("writerName", messageObj.getWriterName()).
		            append("writerGender", messageObj.getWriterGender()).
		            append("message", messageObj.getMessage()).
		            append("time", time);
		
		roomCollection.insertOne(doc);
		
		messageObj.setTime(time);
		
		// Member
    	MongoCollection<Document> memberCollection = DB.getCollection("members");
    	Document filter = new Document("id", messageObj.getMessageID());
    	Document update = new Document("$inc", new Document("messageCount",1));
		memberCollection.updateOne(filter, update);
    	// Member
		
		
        try{
            for(Session session : sessionList)
            {
            
                //asynchronous communication
             
            	if(session.getUserProperties().get("roomID").equals(roomID))
            		{
            		
            		session.getBasicRemote().sendText(gson.toJson(messageObj));
            		}
            }
        }catch(IOException e){}
    }
}