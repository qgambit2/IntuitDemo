package intuit_interview.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Users")
public class Users {
	@XmlElementWrapper(name="UserInfos")
	@XmlElement(name="User")
	private List<User> users;

	private int total;
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	
}
