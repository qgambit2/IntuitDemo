package intuit_interview.impl;

import java.text.MessageFormat;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import intuit_interview.interfaces.IdentitySource;
import intuit_interview.model.AuthenticationException;
import intuit_interview.model.TwitterException;
import intuit_interview.model.User;

public class LDAPIdentitySource implements IdentitySource{
	private static final Logger logger=
            LoggerFactory.getLogger(LDAPIdentitySource.class);
	private String url = null;
	private MessageFormat principalFormat = null;
	public LDAPIdentitySource(String url, String principalTemplate){
		this.url = url;
		this.principalFormat = new MessageFormat(principalTemplate);
	}

	public void login (String username, String password) throws intuit_interview.model.AuthenticationException{
		try{
			Hashtable<String, String> env = new Hashtable<>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, 
			    "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, url);
	
			String principal = principalFormat.format(new String[]{username});
			
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, principal);
			env.put(Context.SECURITY_CREDENTIALS, password);
	
			// Create the initial context
			new InitialDirContext(env);
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new AuthenticationException("Authentication failed");
		}
	}

	public void embellishUser(User user) throws TwitterException {
		//FIXME
	}
	

}
