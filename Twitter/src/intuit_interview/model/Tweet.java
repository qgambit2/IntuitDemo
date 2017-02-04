package intuit_interview.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Tweet")
public class Tweet {
	private long id; 
    private String message;
    private String date = null;
    
    @XmlTransient
    private int userId;
    
    private String username;
    
    public Tweet(){
    	
    }

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}



	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	
    
    
}
