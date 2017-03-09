package com.batuhanyaman.huchat;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "rooms")
public class Room {

	private int ID;
	private String name;
	private int online;
	
	@XmlElement(name = "id")
	public int getID() {
		return ID;
	}
	@XmlElement
	public String getName() {
		return name;
	}
	@XmlElement
	public int getOnline() {
		return online;
	}
	public Room(){}
	public Room(int iD, String name, int online) {
		super();
		ID = iD;
		this.name = name;
		this.online = online;
	}
	
	
}
