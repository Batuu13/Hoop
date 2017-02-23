package com.batuhanyaman.huchat.util;

import com.batuhanyaman.huchat.Date;

public class NotificationM extends Notification {

	int requester_id;
	int reciever_id;
	String message;
	
	public NotificationM(int req_id, int rec_id, String message)
	{
		requester_id = req_id;
		reciever_id = rec_id;
		this.message = message;
		date = Date.getDate();
		type_id = 2;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
