package com.batuhanyaman.huchat.util;

import com.batuhanyaman.huchat.Date;

public class NotificationFR extends Notification{
	
	int requester_id;
	int reciever_id;
	
	
	public NotificationFR(int req_id, int rec_id)
	{
		requester_id = req_id;
		reciever_id = rec_id;
		date = Date.getDate();
		type_id = 1;
	}


	public int getRequester_id() {
		return requester_id;
	}


	public void setRequester_id(int requester_id) {
		this.requester_id = requester_id;
	}


	public int getReciever_id() {
		return reciever_id;
	}


	public void setReciever_id(int reciever_id) {
		this.reciever_id = reciever_id;
	}
	
	
}
