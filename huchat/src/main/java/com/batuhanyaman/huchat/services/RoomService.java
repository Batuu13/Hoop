package com.batuhanyaman.huchat.services;
import java.util.ArrayList;
import org.bson.Document;

import com.batuhanyaman.huchat.Database;
import com.batuhanyaman.huchat.Member;
import com.batuhanyaman.huchat.Room;
import com.batuhanyaman.huchat.util.IDTracker;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


public class RoomService {
	private static final String ROOM_PREFIX = "room_";
	private MongoCollection<Document>  roomCol;
	MongoDatabase db;
	
	
	ArrayList<Room> rooms = new ArrayList<Room>();
	public RoomService()
	{
		
		db = Database.getInstance().getMongoClient().getDatabase("test");
		
		
	}
	
	public ArrayList<Member> getUsers(String roomName)
	{
		//TODO FIX
		ArrayList<Member> members = new ArrayList<>();
		FindIterable<Document> room = roomCol.find(new Document("id",Integer.parseInt(roomName)));
		MongoCollection<Document> memCol = db.getCollection("members");
		
		@SuppressWarnings("unchecked")
		ArrayList<String> users =  (ArrayList<String>) room.first().get("onlineMembers");
		
		for (String id : users) {
			Document document = memCol.find(new Document("id",Integer.parseInt(id))).first();
			Member member = new Member();
			member.setId(document.getInteger("id"));
			member.setName(document.getString("name"));
			//TODO ADD IMAGE AND UNI
			members.add(member);
		}
		
		return(members);
	}
	public ArrayList<Document> getRooms(Double longitude, Double latitude)
	{
		ArrayList<Document> clusters = findCluster(longitude,latitude);	
		ArrayList<Document> rooms = new ArrayList<>();
		for(int i = 0; i< clusters.size(); i++) // Probably size is 1 so no worry.
		{
			Document cluster = clusters.get(i);
			ArrayList<Document> points = (ArrayList<Document>) cluster.get("points");
			for(int j = 0 ; j < points.size() ; j++)
			{
				Document point = points.get(j);
				Double distance = distance(latitude,point.getDouble("lat"),longitude,point.getDouble("long"));
				if(point.getInteger("radius") >= distance)
				{
					point.append("clusterID", cluster.getInteger("_id"));
					
					rooms.add(point);
				}
			}
			
		}
		return rooms;
	}
	public ArrayList<Document> getAllRooms()
	{
		ArrayList<Document> clusters = getClusters();	
		ArrayList<Document> rooms = new ArrayList<>();
		for(int i = 0; i< clusters.size(); i++) // Probably size is 1 so no worry.
		{
			Document cluster = clusters.get(i);
			@SuppressWarnings("unchecked")
			ArrayList<Document> points = (ArrayList<Document>) cluster.get("points");
			for(int j = 0 ; j < points.size() ; j++)
			{
				Document point = points.get(j);
					rooms.add(point);
			}
			
		}
		return rooms;
	}
	private ArrayList<Document> findCluster(Double longitude, Double latitude) {
		MongoCollection<Document> clusterCol = db.getCollection("clusters");
		MongoCursor<Document> clusterList = clusterCol.find().iterator();
		ArrayList<Document> clusters = new ArrayList<Document>();

		while(clusterList.hasNext())
		{
			Document temp = clusterList.next();
			Double distance = distance(latitude,temp.getDouble("lat"),longitude,temp.getDouble("long"));
			if(temp.getInteger("radius") >= distance)
			{
				clusters.add(temp);
			}
		}
		return clusters;
	}
	public ArrayList<Document> getClusters()
	{
		MongoCollection<Document> clusterCol = db.getCollection("clusters");
		MongoCursor<Document> clusterList = clusterCol.find().iterator();
		ArrayList<Document> clusters = new ArrayList<Document>();

		while(clusterList.hasNext())
		{
				clusters.add(clusterList.next());
		}
		return clusters;
	}
	private double distance(double lat1, double lat2, double lon1,
	        double lon2) {

	    final int R = 6371; // Radius of the earth

	    Double latDistance = Math.toRadians(lat2 - lat1);
	    Double lonDistance = Math.toRadians(lon2 - lon1);
	    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters


	    return distance;
	}
	public void logoutRoom(String roomID) {
		try{/*
			roomCol = (DBCollection) db.getCollection("rooms");
			BasicDBObject query = new BasicDBObject("id", Integer.parseInt(roomID));

			DBObject room = roomCol.findOne(query);
			roomCol.update(room, new BasicDBObject("$inc", new BasicDBObject("online", -1)));*/
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
	}
	
}
