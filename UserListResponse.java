package com.batuhanyaman.huchat;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserListResponse {
	@XmlElement
	ArrayList<Member> users;

	public UserListResponse(){
		
		
	}
	public UserListResponse(ArrayList<Member> users)
	{
		this.users = users;
	}
	
	public ArrayList<Member> getUsers() {
		return users;
	}
	public void setUsers(ArrayList<Member> users) {
		this.users = users;
	}
}
