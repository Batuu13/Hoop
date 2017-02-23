package com.batuhanyaman.huchat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class Member {

	
	
	
	private int id;
	private int gender;
	private String mail;
	private String password;
	private String name;
	private String regDate;
	private boolean isActivated;
	private String activationKey;
	private int uniID;
	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public Member(){
		
	}

	public Member(int gender, String mail, String name,String password) {
		this.gender = gender;
		this.mail = mail;
		this.password = password;
		this.name = name;
		this.regDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		isActivated = false;
		this.uniID = 0;
		//TODO Create random AKey, assign ID
		
	}
	@XmlElement
	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}
	@XmlElement
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@XmlElement
	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}
	@XmlElement
	public int getId() {
		return id;
	}
	@XmlElement
	public String getRegDate() {
		return regDate;
	}
	@XmlElement
	public String getActivitationKey() {
		return activationKey;
	}
	@XmlElement
	public String getPassword() {
		return password;
	}
	@XmlElement
	public int getUniID() {
		return uniID;
	}
	@XmlElement
	public void setUniID(int uniID) {
		this.uniID = uniID;
	}

	public void setId(int id) {
		this.id = id;
	}

	

	
	
}
