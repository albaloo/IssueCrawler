package data;

import java.util.Date;

public class ReplyToLink {	
	public String receiver;
	public String thread;
	public Date date;
	public String type;
	
	public ReplyToLink(String participant, String thread, Date date, String type) {
		super();
		this.receiver = participant;
		this.thread = thread;
		this.date = date;
		this.type = type;
	}
	
	public boolean equals(Object obj) {
		if(this.receiver.equals(((ReplyToLink)obj).receiver))
			return true;
		else
			return false;
	}
	
	public ReplyToLink clone(){
		ReplyToLink cloned = new ReplyToLink(receiver, thread, date, type);
		return cloned;
	}
}
