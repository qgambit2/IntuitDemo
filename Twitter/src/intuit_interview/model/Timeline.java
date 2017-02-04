package intuit_interview.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Timeline")
public class Timeline {
	
	@XmlElementWrapper(name="Tweets")
	@XmlElement(name="Tweet")
	private List<Tweet> tweets;
	
	private int page;
	private int totalPages;
	private String user;
	private long totalTweets;
	
	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public long getTotalTweets() {
		return totalTweets;
	}

	public void setTotalTweets(long totalTweets) {
		this.totalTweets = totalTweets;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	
	
	
}
