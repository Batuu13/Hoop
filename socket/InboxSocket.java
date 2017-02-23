package socket;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

@ServerEndpoint(value = "/inbox")
public class InboxSocket{
    //notice:not thread-safe
		
	static ArrayList<Session>  sessionList = new ArrayList<>(); //TODO CHANGE TO HASHMAP
    MongoClient client = Database.getInstance().getMongoClient();
    MongoCollection<Document> inboxCollection;
    
    MongoDatabase DB = client.getDatabase("test");
    @OnOpen 
    public void onOpen(Session session) throws IOException{
    		
    	Map<String,String> queryParams = ParseQuery(session.getQueryString());
    	
    	 System.out.println("SOCKET CONNECTED" + queryParams.get("senderId") + " -> " + queryParams.get("recieverId"));
    	 session.getUserProperties().put("senderId",queryParams.get("senderId"));
    	 session.getUserProperties().put("recieverId",queryParams.get("recieverId"));
    	 sessionList.add(session);
    	
    	 inboxCollection = DB.getCollection("inbox");
    	    
 	
 		//get room
 	
    	 try {
 			
				
 			ArrayList<Document> list = getPrivateMessagesIndividual(Integer.parseInt(queryParams.get("recieverId")),
 																	Integer.parseInt(queryParams.get("senderId")));
 			for(int i =0; i < list.size(); i++)
 			{
 				session.getBasicRemote().sendText(list.get(i).toJson());
 			}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
       
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

	
	@OnClose
    public void onClose(Session session){
		System.out.println(session.getUserProperties().get("senderId"));
    }
    @OnError
    public void onError(Throwable t) {
       t.printStackTrace();
       t.getLocalizedMessage();
    }
    @SuppressWarnings("unchecked")
	@OnMessage
    public void onMessage(String message,Session session){
    	// JSON PARSING
    	PrivateMessage messageObj = null;
    	Gson gson =null;
    	try{
    	 gson = new Gson();
    	 JsonParser parser = new JsonParser();
    	 com.google.gson.JsonObject obj = parser.parse(message).getAsJsonObject();
    	 messageObj = gson.fromJson(obj,PrivateMessage.class);
    	 System.out.println(message);
    	}catch (Exception e) {

    		e.printStackTrace();
		}
    	
    	
    	  
    	  messageObj.setReceiverID(Integer.parseInt((String) session.getUserProperties().get("recieverId")));
    	  messageObj.setSenderID(Integer.parseInt((String) session.getUserProperties().get("senderId")));
    	  
		
		// GETING DATE PATTERN
		String time = String.format("%d.%d.%d_%02d:%02d",Calendar.getInstance().get(Calendar.DAY_OF_MONTH),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
		messageObj.setTime(time);
		// SET THE MESSAGE DOCUMENT
		Document doc = new Document("messageID", 0). //TODO FIX LATER
			 	append("writerID", messageObj.getSenderID()).
	            append("writerName", messageObj.getWriterName()).
	            append("writerGender", messageObj.getWriterGender()).
	            append("message", messageObj.getMessage()).
	            append("time", time);
		
		// GET RECEIVER
		Document receiver = getReceiver(messageObj.getReceiverID());
		
		/*
		 * THERE ARE 2 "createNewInbox" and "createNewSender". 
		 * Because it will create those fields for both sides. 
		 * AddMessage only have one because it will add for both side inside of the function.
		 */
		
    	if(receiver == null) // IF INBOX IS FOUND
    	{
    		createNewInbox(messageObj.getReceiverID());
    		createNewInbox(messageObj.getSenderID());
    		receiver = getReceiver(messageObj.getReceiverID());
    	}
    	if(!existsInbox(messageObj.getSenderID(),receiver)) // IF SENDER HAS NEVER SENT A MESSAGE BEFORE, SO CREATE A NEW FIELD.
		{
			createNewSender(messageObj.getSenderID(),messageObj.getReceiverID());
			createNewSender(messageObj.getReceiverID(),messageObj.getSenderID());
		}
			addMessage(doc,messageObj.getSenderID(),messageObj.getReceiverID());
	
			// MESAJLARIN 2 TARAFIN TELEFONUNA YOLLUYOR
        try{
            for(Session sessionTemp : sessionList)
            {
            	
            	
                //asynchronous communication
             int id = Integer.parseInt(sessionTemp.getUserProperties().get("senderId").toString()); //TODO BURASI CRASHLÝYOR 
            
            	if(id == messageObj.getReceiverID() || id == messageObj.getSenderID())
            		{
            		
            		sessionTemp.getBasicRemote().sendText(gson.toJson(messageObj));
            		 
            		}
            }
        }catch(IOException e){
        	e.printStackTrace();
        }
    }
    
    private void createNewInbox(int receiverID) {

		Document fields = new Document();
		fields.put("inboxID", getCount());
		fields.put("receiverID",receiverID);
		fields.put("inbox", new ArrayList<Document>());
		
		inboxCollection.insertOne(fields);
	}

	private void createNewSender(int senderID, int receiverID) {
		
    	Document filter = new Document();
		filter.put("receiverID",receiverID);
		
		Document update = new Document();
		update.put("senderId", senderID);
		update.put("messages", new ArrayList<Document>());
		
		Document push = new Document();
		push.put("$push", new Document("inbox",update));
		
		inboxCollection.updateOne(filter,push);
	}

	private void addMessage(Document doc,int senderID,int receiverID) { // RECEIVER VE SENDER ID YOK ONLARA PARAMETRE OLARAK EKLE
    	// adding message for receiver
		Document filter = new Document();
		filter.put("receiverID", receiverID);
		filter.put("inbox.senderId", senderID);
		
		Document update = new Document();
		update.put("$push", new Document("inbox.$.messages" ,doc));
		
		inboxCollection.updateOne(filter,update);
		
		// adding message for sender
		Document filter2 = new Document();
		filter2.put("receiverID", senderID);
		filter2.put("inbox.senderId", receiverID);
		
		Document update2 = new Document();
		update2.put("$push", new Document("inbox.$.messages" ,doc));
		
		inboxCollection.updateOne(filter2,update2);
		
		System.out.println("Message Added?");
		
	}

	private boolean existsInbox(int senderID, Document receiver) 
    {
    	@SuppressWarnings("unchecked")
		ArrayList<Document> inboxList = receiver.get("inbox", ArrayList.class);
    	
    	if(inboxList == null)
    		System.out.println("amk");
    	for(int i = 0 ; i < inboxList.size(); i++)
    	{
    		if(inboxList.get(i).getInteger("senderId") == senderID)
    		{
    			return true;
    		}
    	}
		return false;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Document> getPrivateMessagesIndividual(int recieverID,int senderID) {
		
		MongoCollection<Document> inboxCollection =  DB.getCollection("inbox");
		FindIterable<Document> user = inboxCollection.find(new Document("receiverID",recieverID));
		
		if(user.first() != null)
		{
			ArrayList<Document> inbox = (ArrayList<Document>) user.first().get("inbox");
			
			for(int i = 0 ; i < inbox.size() ; i++) // searching through inbox to find the sender.
			{
				int senderId = inbox.get(i).getInteger("senderId");
				System.out.println(inbox.get(i).getInteger("senderId") + " - " + senderID);
				if(senderId == senderID)
				{
					
					return (ArrayList<Document>) inbox.get(i).get("messages");
					
				}
			}
				// ITS THE FIRST MESSAGE
			
			
				
				return new ArrayList<Document>(); // USER IS NOT FOUND?	
		}
		else
		{
			System.out.println("USER IS NOT FOUND");
			return new ArrayList<Document>();
		}
		
	}

    private Document getReceiver(int receiverID)
    {
    	return inboxCollection.find(new Document("receiverID",receiverID)).first();
    }
    private int getCount()
    {
    	 // GETING COUNTER FOR "inboxID"
    	MongoCollection<Document> counterCollection =  DB.getCollection("counters");
    	FindIterable<Document> counter = counterCollection.find(new Document("_id","inboxID"));
		int count = (int) counter.iterator().next().get("count");
		counterCollection.updateOne(new Document("_id","inboxID"), new Document("$set",new Document("count",++count)));
		return --count;
    } 

}