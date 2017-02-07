package intuit_interview.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import intuit_interview.model.Timeline;
import intuit_interview.model.Tweet;
import intuit_interview.model.TwitterException;
import intuit_interview.model.User;
import intuit_interview.model.Users;

public class TwitterMemoryImpl extends TwitterBase {

	
    private Map<String, UserWrapper> userMap = new HashMap<String, UserWrapper>();
    
    private Map<String, Set<String>> userToFollowees = new HashMap<>();
	
	public TwitterMemoryImpl(){
		GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
		cfg.setTestOnBorrow(true);
		cfg.setMaxTotal(20);
		cfg.setMinIdle(2);
		cfg.setMinEvictableIdleTimeMillis(60000);
		
	}
	
	private long counter = 0;
	public long postTweet(Tweet tweet) throws TwitterException {
		long id = ++counter;
		tweet.setId(id);
		if (tweet.getDate()==null){
			Date now = new Date();
			tweet.setDate(getDateFormat().format(now));
		}
		String username = tweet.getUsername();
		UserWrapper uw = userMap.get(username);
		uw.post(tweet);
		
		return id;
	}

	
	public Timeline getNewsFeed(String userId, int page) throws TwitterException {
		List<Tweet> res = new LinkedList<>();

        UserWrapper uw = this.userMap.get(userId);
        Set<Tweet> tweets = uw.myTimeLine;
        int total = tweets.size();
        
        int startIndex = (page-1)*getPageSize();
        
        Set<Tweet> timeLine = userMap.get(userId).getTimeLine();
        if (timeLine.size()>startIndex){
	        List<Tweet> subList = new ArrayList<>(timeLine).subList(startIndex, 
	        		Math.min(timeLine.size(),startIndex+getPageSize()));
	        for (Tweet t:subList){
	        	res.add(t);
	        }
        }
       
        
       
        Timeline t = new Timeline();
		t.setTotalTweets(tweets.size());
		t.setTotalPages(Math.max(1, (int)(Math.ceil((double)total/getPageSize())))); //at minimum 1 empty page
		t.setPage(page);
		t.setTweets(res);
		t.setUser(userId);
		return t;
	}

	
	public Timeline getTimeline(String userId, int page) throws TwitterException {
		List<Tweet> res = new LinkedList<>();

        int startIndex = (page-1)*getPageSize();
        
        Set<Tweet> timeLine = userMap.get(userId).myTweets;
        int total = timeLine.size();
        if (timeLine.size()>startIndex){
	        List<Tweet> subList = new ArrayList<>(timeLine).subList(startIndex, 
	        		Math.min(timeLine.size(),startIndex+getPageSize()));
	        for (Tweet t:subList){
	        	res.add(t);
	        }
        }
       
        Timeline t = new Timeline();
		t.setTotalTweets(total);
		t.setTotalPages(Math.max(1, (int)(Math.ceil((double)total/getPageSize())))); //at minimum 1 empty page
		t.setPage(page);
		t.setTweets(res);
		t.setUser(userId);
		return t;
	}

	public void follow(String followerId, Users followees) throws TwitterException {
		List<intuit_interview.model.User> list = followees.getUsers();
		UserWrapper uw = userMap.get(followerId);
		for (intuit_interview.model.User u: list){
			uw.follow(u.getUsername());
		}
	}

	
	public void unfollow(String followerId, Users followees) throws TwitterException {
		List<intuit_interview.model.User> list = followees.getUsers();
		UserWrapper uw = userMap.get(followerId);
		for (intuit_interview.model.User u: list){
			uw.unfollow(u.getUsername());
		}
	}

	
	
	
	public Users getFollowees(String username) throws TwitterException {
		Users users = new Users();
		List<intuit_interview.model.User> userList = new ArrayList<intuit_interview.model.User>();
		users.setUsers(userList);
		Set<String> followees = this.userToFollowees.get(username);
		if (followees!=null){
			for (String s:followees){
				intuit_interview.model.User usr = new intuit_interview.model.User();
				usr.setUsername(s);
				userList.add(usr);
			}
			users.setTotal(followees.size());
		}
		return users;
	}

	
	public void removeTweet(long tweetKey) throws TwitterException {
		Tweet remove = tweetIdToTweet.remove(tweetKey);
		if (remove!=null){
			String userName = remove.getUsername();
			UserWrapper userW = userMap.get(userName);
			if (userW!=null){
				userW.myTweets.remove(remove);
				userW.removeFromTimeline(remove);
	            for (String userId:userW.followers){
	            	UserWrapper u = userMap.get(userId);
	            	u.removeFromTimeline(remove);
	            }
			}
		}
	}

	
	public Tweet getTweet(long tweetId) throws TwitterException {
		return tweetIdToTweet.get(tweetId);
	}

	
	public Users findUsers(String search) throws TwitterException {
		Users u = new Users();
		List<intuit_interview.model.User> userList = new ArrayList<intuit_interview.model.User>();
		u.setUsers(userList);
		if (search==null || search.length()==0){
			int count = 0;
			for (String name:users){
				if (count == getPageSize()){
					break;
				}
				count++;
				User user = new User();
				user.setUsername(name);
				userList.add(user);
			}
			return u;
		}
		Set<intuit_interview.model.User> users = this.stringToUsername.get(search);
		
		if (users!=null){
			if (users.size()<=getPageSize()){
				userList.addAll(users);
			}
			else{
				int count = 0;
				for (intuit_interview.model.User aUser:users){
					if (count == getPageSize()){
						break;
					}
					count++;
					userList.add(aUser);
				}
			}
		}
		
		u.setTotal(userList.size());
		return u;
	}
	
	Map<String, Set<intuit_interview.model.User>> stringToUsername = 
			new HashMap<String, Set<intuit_interview.model.User>>();
	Set<String> users = new TreeSet<String>();
	Map<String, String> userToToken = new HashMap<String, String>();
	Map<String, String> tokenToUser = new HashMap<String, String>();
	
	public String login(String username, String password) throws TwitterException {
		this.getIdentitySource().login(username, password);
		
		//generate token
		String randomUUID = UUID.randomUUID().toString();
		tokenToUser.put(randomUUID, username);
		userToToken.put(username, randomUUID);
		
		if (!users.contains(username)){
			//Add to search.
			for (int i=0;i<username.length();i++){
				String s = username.substring(0,i+1);
				Set<intuit_interview.model.User> set = stringToUsername.get(s);
				if (set == null){
					set = new HashSet<intuit_interview.model.User>();
					stringToUsername.put(s,set);
				}
				intuit_interview.model.User u = new intuit_interview.model.User();
				u.setUsername(username);
				set.add(u);
			}
			users.add(username);
			userMap.put(username, new UserWrapper(username));
		}
		
		return randomUUID;
	}

	
	public void logout(String username) throws TwitterException {
		String token = userToToken.get(username);
		if (token!=null){
			tokenToUser.remove(token);
		}
		userToToken.remove(username);
	}

	
	public String getUserForToken(String token) throws TwitterException {
		String user = tokenToUser.get(token);
		return user;
	}
	
	
	
	Map<Long, Tweet> tweetIdToTweet = new HashMap<Long, Tweet>();
	
	
	public class UserWrapper{
        public String id;
        public Set<String> followers = new HashSet<>();
        private TreeSet<Tweet> myTweets = new TreeSet<Tweet>(new Comparator<Tweet>() {
			public int compare(Tweet a, Tweet b) {
				return (int)(b.getId()-a.getId());
			}
		});
        private Set<Tweet> myTimeLine = new TreeSet<Tweet>(new Comparator<Tweet>() {
			public int compare(Tweet a, Tweet b) {
				return (int)(b.getId()-a.getId());
			}
		});
        
        public UserWrapper(String id){
            this.id=id;
            followers.add(this.id); // first follow itself
        }
        
        public void follow(String id){
            UserWrapper followee = userMap.get(id);
            followee.followers.add(this.id);
            myTimeLine.addAll(followee.myTweets);
            Set<String> set = userToFollowees.get(this.id);
            if (set == null){
            	set = new HashSet<String>();
            	userToFollowees.put(this.id, set);
            }
            set.add(id);
        }
        
        public void unfollow(String id){
            UserWrapper followee = userMap.get(id);
            followee.followers.remove(this.id);
            myTimeLine.removeAll(followee.myTweets);
            Set<String> followees = userToFollowees.get(this.id);
            if (followees!=null){
            	followees.remove(id);
            }
        }
        
        public void addToTimeline(Tweet t){
            myTimeLine.add(t);
        }
        
        private void removeFromTimeline(Tweet t){
            myTimeLine.remove(t);
        }
        
        public void post(Tweet t){
            myTweets.add(t);
            tweetIdToTweet.put(t.getId(), t);
            Tweet remove = myTweets.size()>getMaxTimelineSize()?myTweets.last():null;

            if (remove!=null){
            	tweetIdToTweet.remove(remove.getId());
            	myTweets.remove(remove);
            	removeFromTimeline(remove);
            }
            for (String userId:followers){
            	UserWrapper u = userMap.get(userId);
            	u.addToTimeline(t);  
            	if (remove!=null){
            		u.removeFromTimeline(remove);
            	}
            }
        }
        
        public Set<Tweet> getTimeLine(){
            return myTimeLine;
        }
    }
	
    
    

}
