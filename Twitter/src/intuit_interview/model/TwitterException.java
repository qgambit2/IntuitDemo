package intuit_interview.model;

@SuppressWarnings("serial")
public class TwitterException extends Exception {
	public TwitterException(){
		super();
	}
	public TwitterException(String msg){
		super(msg);
	}
	public TwitterException(String msg, Exception e){
		super(msg, e);
	}
}
