package intuit_interview.interfaces;

import intuit_interview.model.AuthenticationException;
import intuit_interview.model.TwitterException;
import intuit_interview.model.User;

public interface IdentitySource {
	public void embellishUser(User user) throws TwitterException;
	public void login(String username, String password) throws AuthenticationException;
}
