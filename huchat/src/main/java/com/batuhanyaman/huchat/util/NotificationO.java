package com.batuhanyaman.huchat.util;

import com.batuhanyaman.huchat.Date;

public class NotificationO extends Notification{
	
	String message;
	
	
	public NotificationO(String message)
	{
		this.message = message;
		date = Date.getDate();
		type_id = 3;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}

	
}
