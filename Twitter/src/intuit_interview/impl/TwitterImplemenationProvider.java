package intuit_interview.impl;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import intuit_interview.interfaces.Twitter;

public class TwitterImplemenationProvider {
	
	private static TwitterImplemenationProvider instance;
	private String twitterType;
	
	private String redisHost = null;
	private String redisPort = null;
	
	private final static String TWITTER_TYPE = "TWITTER_TYPE";
	private static final String TWITTER_TYPE_REDIS = "REDIS";
	private static final String TWITTER_TYPE_MEMORY = "MEMORY";
	private static final String REDIS_HOST = "REDIS_HOST";
	private static final String REDIS_PORT = "REDIS_PORT";
	
	private static final Logger logger =  
			LoggerFactory.getLogger(TwitterImplemenationProvider.class);
	private TwitterImplemenationProvider(){
		try{
			Context ctx = new InitialContext();
			ctx = (Context) ctx.lookup("java:comp/env");
			try{
				twitterType = (String) ctx.lookup(TWITTER_TYPE);
			}catch(Exception e){}
			if (twitterType == null){
				twitterType = System.getProperty(TWITTER_TYPE, TWITTER_TYPE_MEMORY);
			}
			if (twitterType.equals(TWITTER_TYPE_REDIS)){
				try{
					redisHost = (String) ctx.lookup(REDIS_HOST);
				}catch(Exception e){}
				try{
					redisPort = (String) ctx.lookup(REDIS_PORT);
				}catch(Exception e){}
				if (redisHost == null){
					redisHost = System.getProperty(REDIS_HOST,"localhost");
				}
				if (redisPort == null){
					redisPort = System.getProperty(REDIS_PORT, "6379");
				}
			}
		}
		catch(NamingException e){
			logger.error(e.getMessage(), e);
		}
	}
	Twitter twitter = null;
	public Twitter getImplementation(){
		if (twitter!=null){
			return twitter;
		}
		TwitterBase base = null;
		if (this.twitterType.equals(TWITTER_TYPE_REDIS)){
			base = new TwitterRedisImpl();
			((TwitterRedisImpl)base).init(redisHost, Integer.parseInt(redisPort));
		}
		else{
			base = new TwitterMemoryImpl();
		}
		ConfigurationManager cfg = ConfigurationManager.getInstance();
		base.setIdentitySource(cfg.getIdentitySource());
		base.setInactivityTimeout(cfg.getInactivityTimeout());
		base.setMaxTimelineSize(cfg.getMaxTimelineSize());
		base.setPageSize(cfg.getPageSize());
		twitter = base;
		
		return twitter;
	}
	
	private static Object LOCK = "LOCK";
	public static TwitterImplemenationProvider getInstance(){
		if (instance!=null){
			return instance;
		}
		synchronized(LOCK){
			if (instance!=null){
				return instance;
			}
			instance = new TwitterImplemenationProvider();
			return instance;
		}
	}
}
