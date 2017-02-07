package intuit_interview.impl;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import intuit_interview.interfaces.IdentitySource;

public class ConfigurationManager {
	private final static String LDAP_URL = "LDAP_URL";
	private final static String LDAP_PRINCIPAL_TEMPLATE= "LDAP_PRINCIPAL_TEMPLATE";
	private final static String IDENTITY_SOURCE_TYPE = "IDENTITY_SOURCE_TYPE";
	private final static String INACTIVITY_TIMEOUT = "INACTIVITY_TIMEOUT";
	private final static String MAXIMUM_TIMELINE_SIZE = "MAXIMUM_TIMELINE_SIZE";
	private final static String PAGE_SIZE = "PAGE_SIZE";

	
	private static final String IDENTITY_SOURCE_TYPE_LDAP = "LDAP";
	
	
	
	private static final Logger logger =  LoggerFactory.getLogger(ConfigurationManager.class);
	private static ConfigurationManager instance;
	
	private int pageSize = 100;
	private int inactivityTimeout = 1800;
	private int maxTimelineSize = 1000;
	private IdentitySource identitySource = null;
	
	
	private ConfigurationManager(){
		String ldapUrl = null;
		String ldapPrincipalTemplate = null;
		String identitySourceType = null;
		String sInactivityTimeout = null;
		String sMaximumTimelineSize = null;
		String sPageSize = null;
		try{
			Context ctx = new InitialContext();
			ctx = (Context) ctx.lookup("java:comp/env");
			try{
				ldapUrl = (String) ctx.lookup(LDAP_URL);
			}catch(Exception e){}
			try{
				ldapPrincipalTemplate = (String) ctx.lookup(LDAP_PRINCIPAL_TEMPLATE);
			}catch(Exception e){}
			try{
				identitySourceType = (String) ctx.lookup(IDENTITY_SOURCE_TYPE);
			}catch(Exception e){}
			try{
				sInactivityTimeout = (String) ctx.lookup(INACTIVITY_TIMEOUT);
			}catch(Exception e){}
			try{
				sMaximumTimelineSize = (String) ctx.lookup(MAXIMUM_TIMELINE_SIZE);
			}catch(Exception e){}
			try{
				sPageSize = (String) ctx.lookup(PAGE_SIZE);
			}catch(Exception e){}
			
		}
		catch(NamingException e){
			logger.error(e.getMessage(), e);
		}
		if (ldapUrl == null){
			ldapUrl = System.getProperty(LDAP_URL, "ldap://localhost:389");
		}
		if (ldapPrincipalTemplate == null){
			ldapPrincipalTemplate = System.getProperty(LDAP_PRINCIPAL_TEMPLATE,
					"cn={0},dc=example,dc=org");
		}
		if (identitySourceType == null){
			identitySourceType = System.getProperty(IDENTITY_SOURCE_TYPE);
		}
		if (sInactivityTimeout == null){
			sInactivityTimeout = System.getProperty(INACTIVITY_TIMEOUT);
		}
		if (sMaximumTimelineSize == null){
			sMaximumTimelineSize = System.getProperty(MAXIMUM_TIMELINE_SIZE);
		}
		if (sPageSize == null){
			sPageSize = System.getProperty(PAGE_SIZE);
		}
		
		
		
		if (sInactivityTimeout!=null){
			inactivityTimeout = Integer.parseInt(sInactivityTimeout);
		}
		if (sMaximumTimelineSize!=null){
			maxTimelineSize = Integer.parseInt(sMaximumTimelineSize);
		}
		if (sPageSize!=null){
			pageSize = Integer.parseInt(sPageSize);
		}
		
		
		if (identitySourceType!=null && identitySourceType.equals(IDENTITY_SOURCE_TYPE_LDAP)){
			identitySource = new LDAPIdentitySource(ldapUrl, ldapPrincipalTemplate);
		}
		else{
			identitySource = new DummyIdentitySource();
		}
	}

	private static Object LOCK = "LOCK";
	public static ConfigurationManager getInstance(){
		if (instance!=null){
			return instance;
		}
		synchronized(LOCK){
			if (instance!=null){
				return instance;
			}
			instance = new ConfigurationManager();
			return instance;
		}
	}
	public int getPageSize() {
		return pageSize;
	}
	public int getInactivityTimeout() {
		return inactivityTimeout;
	}
	public int getMaxTimelineSize() {
		return maxTimelineSize;
	}
	public IdentitySource getIdentitySource() {
		return identitySource;
	}
	
	
}
