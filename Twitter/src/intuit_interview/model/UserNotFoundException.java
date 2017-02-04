package intuit_interview.model;

@SuppressWarnings("serial")
public class UserNotFoundException extends TwitterException {
	public UserNotFoundException(){
		super();
	}
	public UserNotFoundException(String msg){
		super(msg);
	}
	public UserNotFoundException(String msg, Exception e){
		super(msg, e);
	}
}
