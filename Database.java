package com.batuhanyaman.huchat;

import com.mongodb.MongoClient;

public class Database {

	private static Database instance = null;
	private MongoClient mongoClient;
	
	private Database()
	{
		mongoClient = new MongoClient();
		
	}
	
	public static Database getInstance()
	{
		if(instance == null)
		{
			instance = new Database();
		}
		return instance;
	}

	public MongoClient getMongoClient() {
		
		return mongoClient;
	}

	public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
	
	
}
