package com.batuhanyaman.huchat.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import org.bson.Document;
import org.glassfish.jersey.internal.util.Base64;

import com.batuhanyaman.huchat.Database;
import com.batuhanyaman.huchat.Member;
import com.batuhanyaman.huchat.util.Friend;
import com.batuhanyaman.huchat.util.NotificationFR;
import com.batuhanyaman.huchat.util.NotificationO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javax.inject.Singleton;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MemberService {
	
	// this is the path for images
	private final String path = "//root//kampuschat//"; // C:\\Users\\ACER-PC\\Desktop\\ : //root//kampuschat
	
	public enum Protocol {
	    SMTP,
	    SMTPS,
	    TLS
	}
	MongoCollection<Document> memberCollection;
	MongoDatabase db;
	private SecureRandom random = new SecureRandom();
	private static MemberService memberService = null;
	public static MemberService getInstance()
	{
		if(memberService == null)
		{
			memberService = new MemberService();
		}
		return memberService;
	}
	
	private MemberService()
	{
		
		db = Database.getInstance().getMongoClient().getDatabase("test");
		memberCollection =  db.getCollection("members");
	}
	public String getMember(int memberID) {
		
		FindIterable<Document> memberObj = memberCollection.find(new Document("id",memberID));
		Document mMember= memberObj.first();
		Member theMember = new Member();
		theMember.setName(mMember.getString("name"));
		theMember.setId((mMember.getInteger("id")));
		theMember.setGender(mMember.getInteger("gender"));
		
		Gson gson = new GsonBuilder().create();
		//TODO ADD MORE
		return gson.toJson(theMember);
	}
	
	public boolean RegisterMember(Member newMember)
	{
		try{
		MongoCollection<Document> counterCollection =  db.getCollection("counters");
		
		FindIterable<Document> counter = counterCollection.find(new Document("_id","memberCounter"));
		Document memberCount = counter.iterator().next();
		String actKey = getActivationKey();
		newMember.setActivationKey(actKey);
		memberCollection.insertOne(new Document().append("id", memberCount.getInteger("count")+1)
											     .append("name", newMember.getName())
											     .append("password", newMember.getPassword())//TODO ENCRYPT
												 .append("mail",newMember.getMail())
												 .append("gender",newMember.getGender())
												 .append("regDate",new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss").format(Calendar.getInstance().getTime()))
												 .append("isActivated","false")
												 .append("activationKey",newMember.getActivitationKey())
												 .append("description","")
												 .append("uniID",newMember.getUniID())
														
														 );
		
		counterCollection.findOneAndUpdate(memberCount, new Document("$inc", new Document("count", 1)));
		sendMail(newMember);
		}
		catch(MongoException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	private String getActivationKey()
	{
		return new BigInteger(256, random).toString(64);
	}
	
	public boolean IsEmailAvailable(String email)
	{
		
		try{
		FindIterable<Document> counter = memberCollection.find(new Document("mail",email));
		
			if(counter.iterator().hasNext())
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(Exception e)
		{
		
			e.printStackTrace();
			return true;
		}
	}
	
	public static void sendMail(Member newMember){
		
		
		// Recipient's email ID needs to be mentioned.
	      String to = newMember.getMail();

	      // Sender's email ID needs to be mentioned
	      String from = "admin@batuhanyaman.com";
	      final String username = "admin@batuhanyaman.com";//change accordingly
	      final String password = "batu9595";//change accordingly

	      // Assuming you are sending email through relay.jangosmtp.net
	      String host = "mail.batuhanyaman.com";

	      Properties props = new Properties();
	      props.put("mail.smtp.ssl.trust", "mail.batuhanyaman.com");
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "587");

	      // Get the Session object.
	      Session session = Session.getInstance(props,
	         new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	               return new PasswordAuthentication(username, password);
	            }
		});

	      try {
	            // Create a default MimeMessage object.
	            Message message = new MimeMessage(session);

	   	   // Set From: header field of the header.
		   message.setFrom(new InternetAddress(from));

		   // Set To: header field of the header.
		   message.setRecipients(Message.RecipientType.TO,
	              InternetAddress.parse(to));

		   // Set Subject: header field
		   message.setSubject("Lütfen Kayıdınızı Onaylayın!");
		   String url = "http://campus.batuhanyaman.com/auth.php?mail="+newMember.getMail()+"&authKey="+newMember.getActivitationKey();
		   // Send the actual HTML message, as big as you like
		   message.setContent(
	              "<h1>Hoşgeldin, " + newMember.getName() + "!</h1>"
	           + "<br> Lütfen onaylamak için <a href=\""+ url + "\">buraya</a> tıklayın."
	           + "<br><br> Alpha Version 0.1" ,
	             "text/html; charset=UTF-8");

		   // Send message
		   Transport.send(message);

		   System.out.println("Registration mail successfully sent to" + newMember.getMail());
	      }
	      catch(Exception e)
	      {
	    	  e.printStackTrace();
	      }
	}
	
	public boolean activate(String mail, String key)
	{
		MongoCollection<Document> memberCollection =  db.getCollection("members");
		
		FindIterable<Document> counter = memberCollection.find(new Document("mail",mail));
		if(counter.iterator().hasNext())
		{
			Document member = counter.iterator().next();
			if(member.getString("activationKey").equals(key))
			{
				memberCollection.updateOne(new Document("id",member.getInteger("id")), new Document("$set", new Document("isActivated","true")));
				return true;
			}
			
		}
		return false;
	}
	public String updateMember(int memberID,String name,String image) {
		MongoCollection<Document> memberCollection =  db.getCollection("members");
		
		String decoded = Base64.decodeAsString(image);
		
		
		try {
			image.getBytes();
			String imageName = memberID + "_profilePic";
			File f = new File(path + imageName);
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(image.getBytes());
			fos.flush();
			
				fos.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

		
		try
		{
			Document document = memberCollection.findOneAndUpdate(new Document("id",memberID), new Document("$set",new Document("name",name)));
			if(!document.isEmpty())
			{
				return "true";
			}
			else
			{
				return "false";
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			return "false";
		}
		
	}
	public String getImage(int memberID) {
		
		Path p = Paths.get(path + memberID+"_profilePic");
	
		try {
			if(Files.exists(p)){
			String image = new String(Files.readAllBytes(p));
			
			 String [] temp = image.split("\n");
             String returnStr ="";
             for (String string : temp) {
                 returnStr += string;
             }
			return returnStr;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public int getUniID(String mail) {
		MongoCollection<Document> uniCollection =  db.getCollection("universities");
		Document uni = uniCollection.find(new Document("uniMail",mail)).first();
		
		
		return uni.getInteger("uniID");
	}
	public int sendFriendRequest(int receiverID, int senderID) {
		
		try {
		memberCollection.findOneAndUpdate(new Document("id",receiverID),
				new Document("$addToSet",new Document("requests",
					new Document("senderID",senderID)))); 
		
		
			NotificationFR not = new NotificationFR(senderID,receiverID);
			NotificationService.getInstance().addNotification(receiverID, not);
		
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	public int acceptFriendRequest(int senderID, int userID,int notID) {
		try {
			
			//FIND REQUEST TO GET TO USER ID
			Document filter = new Document();
			filter.put("id",userID);
			filter.put("requests.senderID",senderID);
			Document request = memberCollection.find(filter).first();

		    //ADD TO THE FRIENDLIST
		    Document query1 = new Document("id",userID);
		    Document update1 = new Document("$addToSet", new Document("friends",senderID));
		    memberCollection.findOneAndUpdate(query1, update1);
		    
		    Document query2 = new Document("id",senderID);
		    Document update2 = new Document("$addToSet", new Document("friends",userID));
		    memberCollection.findOneAndUpdate(query2, update2);
		    
		    
		    //DELETE
		    Document query = new Document();
		    query.put("id", userID);
		    query.put("requests.senderID", senderID);
		    Document update3 = new Document("$pull", new Document("requests",new Document("senderID",senderID)));
		    
		    memberCollection.findOneAndUpdate(query, update3);
		    
		   NotificationService.getInstance().deleteNotifications(userID, notID);
		    
		  
			
		   NotificationService.getInstance().addNotification(senderID, new NotificationO(  getName(userID) + "Arkadaşlık isteğini kabul etti."));
		    
		    
		    return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		
	    
	}
	public int rejectFriendRequest(int requestID, int userID,int notID) {
		try {
			Document query = new Document();
		    query.put("id", userID);
		    query.put("requests.senderID", requestID);
		    Document update2 = new Document("$pull", new Document("requests",requestID));
		    
		    memberCollection.findOneAndUpdate(query, update2);
		    NotificationService.getInstance().deleteNotifications(userID, notID);
		    return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		
	}
	public String getFriends(int userID) {
		Document user = memberCollection.find(new Document("id",userID)).first();
		@SuppressWarnings("unchecked")
		ArrayList<Integer> friends = (ArrayList<Integer>) user.get("friends");
		ArrayList<Friend> friendList = new ArrayList<>();
		if(friends != null)
		{	
			for(int i = 0 ; i < friends.size();i++)
			{
				Document member = memberCollection.find(new Document("id",friends.get(i))).first();
				friendList.add(new Friend(friends.get(i),member.getString("name")));
			}
		}
		Gson gson = new GsonBuilder().create();
		
		return gson.toJson(friendList);
		
	}
	public int deleteFriend(int friendID, int userID) {
		try {
			Document query = new Document();
		    query.put("id", userID);
		    Document update = new Document("$pull", new Document("friends",friendID));
		    
		    memberCollection.findOneAndUpdate(query, update);
		    return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		
	}
	public String getName(int memberID)
	{
		FindIterable<Document> memberObj = memberCollection.find(new Document("id",memberID));
		Document mMember= memberObj.first();
		return mMember.getString("name");
	}
	public int getMemberCount()
	{
		return (int) memberCollection.count();
	}
}
