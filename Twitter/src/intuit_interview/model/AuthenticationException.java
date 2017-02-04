package intuit_interview.model;

@SuppressWarnings("serial")
public class AuthenticationException extends TwitterException {
	public AuthenticationException(){
		super();
	}
	public AuthenticationException(String msg){
		super(msg);
	}
	public AuthenticationException(String msg, Exception e){
		super(msg, e);
	}
}
