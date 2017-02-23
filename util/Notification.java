package com.batuhanyaman.huchat.util;

public abstract class  Notification {

	 int id;
	 int type_id;
	 String date;
	 boolean isSeen;
	 
	public int getType_id() {
		return type_id;
	}
	public void setType_id(int type_id) {
		this.type_id = type_id;
	}
	public boolean isSeen() {
		return isSeen;
	}
	public void setSeen(boolean isSeen) {
		this.isSeen = isSeen;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	public String getDate() {
		return date;
	}
	 
	 
}
