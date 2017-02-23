
package com.batuhanyaman.huchat;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Message {

	private int messageID;
	private int writerID;
	private String writerName;
	private int writerGender;
	private String time;
	private String message;
	
	public Message(){}
	
	public Message(int writerID,String writerName,
			int writerGender, String message, String time) {
		
		super();
		this.writerID = writerID;
		this.writerName = writerName;
		this.writerGender = writerGender;
		this.message = message;
		this.time = time;
		
	}

	public int getWriterID() {
		return writerID;
	}

	public void setWriterID(int writerID) {
		this.writerID = writerID;
	}

	public String getWriterName() {
		return writerName;
	}

	public void setWriterName(String writerName) {
		this.writerName = writerName;
	}

	public int getWriterGender() {
		return writerGender;
	}

	public void setWriterGender(int writerGender) {
		this.writerGender = writerGender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMessageID() {
		return messageID;
	}

	public String getTime() {
		return time;
	}
	
	
}
