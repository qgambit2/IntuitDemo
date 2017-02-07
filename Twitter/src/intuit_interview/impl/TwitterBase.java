package intuit_interview.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import intuit_interview.interfaces.IdentitySource;
import intuit_interview.interfaces.Twitter;

public abstract class TwitterBase implements Twitter {
	private int pageSize = 100;
	private int inactivityTimeout = 1800;   
	private int maxTimelineSize = 1000;
	private IdentitySource identitySource = null;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");  //ISO-8601
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getInactivityTimeout() {
		return inactivityTimeout;
	}
	public void setInactivityTimeout(int inactivityTimeout) {
		this.inactivityTimeout = inactivityTimeout;
	}
	public int getMaxTimelineSize() {
		return maxTimelineSize;
	}
	public void setMaxTimelineSize(int maxTimelineSize) {
		this.maxTimelineSize = maxTimelineSize;
	}
	public IdentitySource getIdentitySource() {
		return identitySource;
	}
	public void setIdentitySource(IdentitySource identitySource) {
		this.identitySource = identitySource;
	}
	public DateFormat getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	

}
