package intuit_interview.impl;

import intuit_interview.interfaces.IdentitySource;
import intuit_interview.model.AuthenticationException;
import intuit_interview.model.TwitterException;
import intuit_interview.model.User;

public class DummyIdentitySource implements IdentitySource{

	public DummyIdentitySource(){}
	
	public DummyIdentitySource(String url, String template){}
	
	public void embellishUser(User user) throws TwitterException {
		//nothing to do
	}
	
	public void login(String username, String password) throws AuthenticationException{
		if (username.equals("eugene") && password.equals("password1")){
			return;
		}
		if (username.equals("valerie") && password.equals("password2")){
			return;
		}
		if (username.equals("vadim") && password.equals("password3")){
			return;
		}
		if (username.equals("ethan") && password.equals("password4")){
			return;
		}
		if (username.equals("curious_george") && password.equals("password5")){
			return;
		}
		if (username.equals("bob") && password.equals("dole")){
			return;
		}
		throw new AuthenticationException("Invalid username and/or password");
	}

}
