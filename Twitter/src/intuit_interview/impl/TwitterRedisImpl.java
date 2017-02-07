package intuit_interview.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import intuit_interview.model.Timeline;
import intuit_interview.model.Tweet;
import intuit_interview.model.TwitterException;
import intuit_interview.model.User;
import intuit_interview.model.Users;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class TwitterRedisImpl extends TwitterBase {

	private JedisPool pool = null;

	private static final Logger logger                  =
            LoggerFactory.getLogger(TwitterRedisImpl.class);
	
	private static final String TWEET_INCR_KEY = "tweet_id";
	private static final String TWEETS = "tweets:";
	private static final String USER = "user:";
	private static final String ALL_USERS = "users:";
	private static final String USER_TWEETS = "user_tweets:";
	private static final String POSTS = "posts:";
	private static final String FOLLOWERS = "followers:";
	private static final String FOLLOWING = "following:";
	private static final String USERNAME = "username";
	
	private static final String TWEET_MESSAGE = "message";
	private static final String TWEET_USERNAME = "username";
	private static final String TWEET_TIME = "time";

	private static final String USERNAME_TOKEN = "username_token:";
	private static final String TOKEN_USERNAME = "token_username:";
	
	private static final String USERNAME_SEARCH = "username_search:";
	
	
	private ExecutorService executor = Executors.newFixedThreadPool(5, new ThreadFactory() {
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "Outband followers timeline updater");
            t.setDaemon(true);
            return t;
        }
	});
	
	
	public TwitterRedisImpl(){}
	
	



	public void init(String host, int port){
		GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
		cfg.setTestOnBorrow(true);
		cfg.setMaxTotal(20);
		cfg.setMinIdle(2);
		cfg.setMinEvictableIdleTimeMillis(60000);
		
		pool = new JedisPool(cfg, host, port);
	}

	
	public Timeline getNewsFeed(String userId, int page) throws TwitterException{
		return getNewsFeed(userId, getPageSize(), page-1);  //0 based pages in backend
	}
	
	
	public Timeline getTimeline(String userId, int page) throws TwitterException{
		return getTimeline(userId, getPageSize(), page-1);  //0 based pages in backend
	}
	
	
	//handle timeouts. Seems like JEDIS client very rarely (after long period of inactivity)
	//fails to connect. Not sure if it's a DOCKER networking issue.
	private Jedis getResource(){
		Jedis jedis = null;
		int count = 0;
		while(jedis == null && count<100){
			try {
			    jedis = pool.getResource();
			    count++;
			} catch (JedisConnectionException e) {
			   logger.error(e.getMessage(), e);
			}
		}
		return jedis;
	}



	//Follower follows followee
	public void follow(String sFollowerId, Users users) throws TwitterException{
		Jedis jedis = null;
		try{
			if (users == null || users.getUsers().size()==0){
				return;
			}
			jedis = getResource();
			List<String> validUsers = new ArrayList<String>();
			for (User u: users.getUsers()){
				if (jedis.exists(USER+u.getUsername())){
					validUsers.add(u.getUsername());
				}
			}
			
			for (String sFolloweeId: validUsers){
				if (sFollowerId.equals(sFolloweeId)){
					continue; //Always implicitly follow ourselves
				}
				Set<Tuple> followeeTweets = jedis.zrangeWithScores(USER_TWEETS+sFolloweeId, 0, -1);
				
				Transaction t = jedis.multi();	
				Map<String, Double> tweetMap = new HashMap<String, Double>();
				if (followeeTweets!=null && followeeTweets.size()>0){
					for (Tuple tup:followeeTweets){
						String val = tup.getElement();
						double score = tup.getScore();
						tweetMap.put(val, score);
					}
				}
	
				
				t.zadd(FOLLOWERS+sFolloweeId, 0, sFollowerId);
				t.zadd(FOLLOWING+sFollowerId, 0, sFolloweeId);
				if (tweetMap.size()>0){
					t.zadd(POSTS+sFollowerId, tweetMap );
				}
				t.exec();
				
			}
			
		}
		finally{
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	public void unfollow(String followerUsername,  Users users)  throws TwitterException{
		Jedis jedis = null;
		try{
			jedis = getResource();
			for (User u:users.getUsers()){
				String followeeUsername = u.getUsername();
				if (followeeUsername.equals(followerUsername)){
					continue; //Can't unfollow yourself
				}
				Set<String> followeeTweets = jedis.zrange(USER_TWEETS+followeeUsername, 0, -1);
				
				String[] tweets = new String[0];
				if (followeeTweets!=null && followeeTweets.size()>0){
					tweets = new String[followeeTweets.size()];
					int index = 0;
					for (String s: followeeTweets){
						tweets[index++]=s;
					}
				}
				
				Transaction t = jedis.multi();	
				t.zrem(FOLLOWERS+followeeUsername, followerUsername);
				t.zrem(FOLLOWING+followerUsername, followeeUsername);
				if (tweets.length>0){
					t.zrem(POSTS+followerUsername, tweets );
				}
				t.exec();
			}
		}
		finally{
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public long postTweet(final Tweet tweet) throws TwitterException{
		Jedis jedis = null;
		try{
			jedis = getResource();
			final String username = tweet.getUsername();
			if (tweet.getDate()==null){
				Date now = new Date();
				tweet.setDate(getDateFormat().format(now));
			}
			final Long tweetKey = jedis.incr(TWEET_INCR_KEY);
			
			//remove elements to keep time line from growing above max size (1K default)
			Set<String> myTimeLine = jedis.zrange(USER_TWEETS+username, getMaxTimelineSize()-1, -1); //leave 999 records
			String[] sToDelete = null;
			if (myTimeLine!=null && myTimeLine.size()>0){
				int index = 0;
				sToDelete = new  String[myTimeLine.size()];
				for (String s:myTimeLine){
					sToDelete[index++]=s;
				}
			}			

			//get followers
			final Set<String> followers = jedis.zrange(FOLLOWERS+username, 0, -1);
			final String[] _delete = sToDelete;
			//execute fanout (the most intensive operation) in separate thread.
			executor.execute(new Runnable(){
				public void run(){
					Jedis jedis = null;		
					try{
						jedis = getResource();
						
						Transaction t = jedis.multi();	
	
						//add tweet reference to own tweet set
						t.zadd(USER_TWEETS+username, -1*tweetKey, String.valueOf(tweetKey));
						
						//add tweet itself
						Map<String, String> hash = new HashMap<>();
						hash.put(TWEET_MESSAGE, tweet.getMessage());
						hash.put(TWEET_USERNAME, username);
						hash.put(TWEET_TIME, tweet.getDate());
						t.hmset(TWEETS+tweetKey, hash);

						//add tweet to followers timelines (Fan out)
						for (String follower:followers){
							t.zadd(POSTS+follower, -1*tweetKey, String.valueOf(tweetKey));
						}
						//assuming we always follow ourselves add tweet to own timeline
						t.zadd(POSTS+username, -1*tweetKey, String.valueOf(tweetKey));

						if (_delete!=null && _delete.length>0){
							removeTweets(username, followers, t, _delete);
						}
						t.exec();
					}
					catch(Exception e){
						logger.error(e.getMessage(), e);
					}
					finally{
						if (jedis!=null){
							jedis.close();
						}
					}
				}
			});
			
			return tweetKey;
		}
		finally{
			if (jedis!=null){
				jedis.close();
			}
		}
	}
	
	
	
	public void removeTweet(final long tweetKey) throws TwitterException{
		Jedis jedis = null;
		try{
			jedis = getResource();
			String userName = null;
			if (jedis.exists(TWEETS+tweetKey)){
				userName = jedis.hget(TWEETS+tweetKey, TWEET_USERNAME);
			}
			else{
				return;
			}
			final String username = userName;
			//get followers
			final Set<String> followers = jedis.zrange(FOLLOWERS+username, 0, -1);

			//execute fanout (the most intensive operation) in separate thread.
			executor.execute(new Runnable(){
				public void run(){
					Jedis jedis = null;		
					try{
						jedis = getResource();
						
						Transaction t = jedis.multi();	
						removeTweets(username,  followers, t, ""+tweetKey);				
						
						t.exec();
					}
					catch(Exception e){
						logger.error(e.getMessage(), e);
					}
					finally{
						if (jedis!=null){
							jedis.close();
						}
					}
				}
			});
		}
		finally{
			if (jedis!=null){
				jedis.close();
			}
		}
	}
	
	private void removeTweets(final String username, 
			Set<String> followers, Transaction t, String... tweetKeys) throws TwitterException{
		//get followers
		final String[] tweetNames = new String[tweetKeys.length];
		for (int i=0;i<tweetKeys.length;i++){
			tweetNames[i] = TWEETS+tweetKeys[i];
		}
		//execute fanout (the most intensive operation) in separate thread.
											
		//add tweet reference to own tweet set
		t.zrem(USER_TWEETS+username, tweetKeys);
		
		for (String follower:followers){
			t.zrem(POSTS+follower, tweetKeys);
		}
		//assuming we always follow ourselves add tweet to own timeline
		t.zrem(POSTS+username, tweetKeys);
		
		t.del(tweetNames);
	}

	private Timeline getNewsFeed(String userName, int feedSize, int page) throws TwitterException{
		Jedis jedis = null;
		List<Tweet> res = new LinkedList<>();
		try{
			jedis = getResource();
			long total = jedis.zcount(POSTS+userName, Long.MIN_VALUE, Long.MAX_VALUE);		
			Set<String> posts = jedis.zrange(POSTS+userName, page*feedSize, (page+1)*feedSize-1); //need to subract one as redis is inclusive!		
			for (String tweetId:posts){
				Map<String, String> map = jedis.hgetAll(TWEETS+tweetId);
				Tweet tweet = new Tweet();
				tweet.setMessage(map.get(TWEET_MESSAGE));
				tweet.setUsername(map.get(TWEET_USERNAME));
				tweet.setDate(map.get(TWEET_TIME));
				tweet.setId(Integer.parseInt(tweetId));
				res.add(tweet);
			}
			
			Timeline t = new Timeline();
			t.setTotalTweets(total);
			t.setTotalPages(Math.max(1, (int)(Math.ceil((double)total/feedSize)))); //at minimum 1 empty page
			t.setPage(page+1);
			t.setTweets(res);
			t.setUser(userName);
			return t;
		}
		finally{
			if (jedis!=null){
				jedis.close();
			}
		}
		
	}
	
	public Tweet getTweet(long tweetId) throws TwitterException{
		Jedis jedis = null;
		Tweet tweet = null;
		try{
			jedis = getResource();
			Map<String, String> map = jedis.hgetAll(TWEETS+tweetId);
			if (map!=null && map.size()>0){
				tweet = new Tweet();
				tweet.setMessage(map.get(TWEET_MESSAGE));
				tweet.setUsername(map.get(TWEET_USERNAME));
				tweet.setDate(map.get(TWEET_TIME));
				tweet.setId(tweetId);
			}
		}
		finally{
			if (jedis!=null){
				jedis.close();
			}
		}
		return tweet;
	}
	
	private Timeline getTimeline(String userName, int feedSize, int page) throws TwitterException{
		Jedis jedis = null;
		List<Tweet> res = new LinkedList<>();
		try{
			jedis = getResource();
			long total = jedis.zcount(USER_TWEETS+userName, Long.MIN_VALUE, Long.MAX_VALUE);
			Set<String> posts = jedis.zrange(USER_TWEETS+userName, page*feedSize, (page+1)*feedSize-1); //need to subract one as redis is inclusive!		
			for (String tweetId:posts){
				Map<String, String> map = jedis.hgetAll(TWEETS+tweetId);
				Tweet tweet = new Tweet();
				tweet.setMessage(map.get(TWEET_MESSAGE));
				tweet.setUsername(map.get(TWEET_USERNAME));
				tweet.setDate(map.get(TWEET_TIME));
				tweet.setId(Integer.parseInt(tweetId));
				res.add(tweet);
			}
			Timeline t = new Timeline();
			t.setTotalTweets(total);
			t.setTotalPages(Math.max(1, (int)(Math.ceil((double)total/feedSize)))); //at minimum 1 empty page
			t.setPage(page+1);
			t.setTweets(res);
			t.setUser(userName);
			return t;
		}
		finally{
			if (jedis!=null){
				jedis.close();
			}
		}
	}

	
	public Users findUsers(String search){
		Jedis jedis = null;
		try{
			jedis = getResource();
			long total = jedis.zcount(USERNAME_SEARCH+search, Long.MIN_VALUE, Long.MAX_VALUE);
			Set<String> userNames = null;
			if (search!=null && search.length()>0){
				userNames = jedis.zrange(USERNAME_SEARCH+search,0, getPageSize());
			}
			else{
				userNames = jedis.zrange(ALL_USERS, 0, getPageSize());
			}
			List<User> userList = new ArrayList<User>();
			for (String name:userNames){
				User user = new User();
				user.setUsername(name);
				userList.add(user);
			}
			Users users = new Users();
			users.setTotal((int)total);
			users.setUsers(userList);
			return users;
		}
		finally{
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	
	public Users getFollowees(String username) throws TwitterException{
		Jedis jedis = null;
		try{
			jedis = getResource();
			Set<String> userNames = jedis.zrange(FOLLOWING+username,0, -1);
			List<User> userList = new ArrayList<User>();
			for (String name:userNames){
				User user = new User();
				user.setUsername(name);
				userList.add(user);
			}
			Users users = new Users();
			users.setUsers(userList);
			users.setTotal(userList.size()); //potentially we will not return all
			return users;
		}
		finally{
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	
	
	public String login(String username, String password) throws TwitterException{
		getIdentitySource().login(username, password);
		
		//generate token
		String randomUUID = UUID.randomUUID().toString();
		
		Jedis jedis = null;
		try{
			jedis = getResource();
			
			boolean userExists = jedis.exists(USER +username);
			
			Transaction t = jedis.multi();	
			t.set(USERNAME_TOKEN+username, randomUUID);
			t.expire(USERNAME_TOKEN+username, getInactivityTimeout());
			
			t.set(TOKEN_USERNAME+randomUUID, username);
			t.expire(TOKEN_USERNAME+randomUUID, getInactivityTimeout());
			
			if (!userExists){ //add user if needed.
				Map<String, String> hash = new HashMap<>();
				hash.put(USERNAME, username);
				t.hmset(USER+username, hash);  //potentially hash may have multiple entries about user

				
				//Add to search.
				for (int i=0;i<username.length();i++){
					t.zadd(USERNAME_SEARCH+username.substring(0,i+1), 0, username);
				}
				
				t.zadd(ALL_USERS, 0, username);
			}
			
			t.exec();
		}
		finally{
			if (jedis != null) {
				jedis.close();
			}
		}
		return randomUUID;
	}
	
	public void logout(String username) throws TwitterException{
		Jedis jedis = null;
		try{
			jedis = getResource();
			String token = jedis.get(USERNAME_TOKEN+username);
			
			jedis.del(USERNAME_TOKEN+username);
			if (token!=null){
				jedis.del(TOKEN_USERNAME+token);
			}
		}
		finally{
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	public String getUserForToken(String token) throws TwitterException{
		Jedis jedis = null;
		try{
			jedis = getResource();
			String username = jedis.get(TOKEN_USERNAME+token);
			if (username!=null){
				jedis.expire(USERNAME_TOKEN+username, getInactivityTimeout()); //extend
				jedis.expire(TOKEN_USERNAME+token, getInactivityTimeout()); //extend
			}
			return username;
		}
		finally{
			if (jedis != null) {
				jedis.close();
			}
		}
	}

}
