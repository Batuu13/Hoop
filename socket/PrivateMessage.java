package socket;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class PrivateMessage {		
		private int messageID;
		private int senderID;
		private int receiverID;
		private String writerName;
		private int writerGender;
		private String time;
		private String message;
		
		public PrivateMessage(){}
		
		public PrivateMessage(int writerID,String writerName,
				int writerGender, String message, String time) {
			
			super();
			this.senderID = writerID;
			this.writerName = writerName;
			this.writerGender = writerGender;
			this.message = message;
			this.time = time;
			
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

		public void setTime(String time) {
			this.time = time;
		}

		public int getSenderID() {
			return senderID;
		}

		public void setSenderID(int senderID) {
			this.senderID = senderID;
		}

		public int getReceiverID() {
			return receiverID;
		}

		public void setReceiverID(int receiverID) {
			this.receiverID = receiverID;
		}

		
		
		
	

}
