package intuit_interview.other;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MemoryTwitter {
	 private int timeStamp=0;
	    private Map<Integer, User> userMap = new HashMap<Integer, User>();
	    
	    // Tweet link to next Tweet so that we can save a lot of time
	    // when we execute getNewsFeed(userId)
	    private class Tweet{
	        public int id;
	        public int time;
	        
	        public Tweet(int id){
	            this.id = id;
	            time = timeStamp++;
	        }
	    }
	    
	    
	    // OO design so User can follow, unfollow and post itself
	    public class User{
	        public int id;
	        public Set<Integer> followers = new HashSet<>();
	        private List<Tweet> myTweets = new LinkedList<Tweet>();
	        private Set<Tweet> myTimeLine = new TreeSet<Tweet>(new Comparator<Tweet>() {
				public int compare(Tweet a, Tweet b) {
					return b.time-a.time;
				}
			});
	        
	        public User(int id){
	            this.id=id;
	            followers.add(this.id); // first follow itself
	        }
	        
	        public void follow(int id){
	            User followee = userMap.get(id);
	            followee.followers.add(this.id);
	            myTimeLine.addAll(followee.myTweets);
	        }
	        
	        public void unfollow(int id){
	            User followee = userMap.get(id);
	            followee.followers.remove(this.id);
	            myTimeLine.removeAll(followee.myTweets);
	        }
	        
	        public void addToTimeline(Tweet t){
	            myTimeLine.add(t);
	        }
	        
	        private void removeFromTimeline(Tweet t){
	            myTimeLine.remove(t);
	        }
	        
	        public void post(int id){
	            Tweet t = new Tweet(id);
	            myTweets.add(0, t);
	            Tweet remove = myTweets.size()>10?myTweets.get(10):null;

	            for (int userId:followers){
	            	User u = userMap.get(userId);
	            	u.addToTimeline(t);  
	            	if (remove!=null){
	            		u.removeFromTimeline(remove);
	            		myTweets.remove(10);
	            	}
	            }
	        }
	        
	        public Set<Tweet> getTimeLine(){
	            return myTimeLine;
	        }
	    }
	    

	    
	    /** Compose a new tweet. */
	    public void postTweet(int userId, int tweetId) {
	        if(!userMap.containsKey(userId)){
	            User u = new User(userId);
	            userMap.put(userId, u);
	        }
	        userMap.get(userId).post(tweetId);
	    }
	    
	    public List<Integer> getNewsFeed(int userId) {
	        List<Integer> res = new LinkedList<>();

	        if(!userMap.containsKey(userId))   return res;
	        
	        Set<Tweet> timeLine = userMap.get(userId).getTimeLine();
	        List<Tweet> subList = new ArrayList<>(timeLine).subList(0, 
	        		Math.min(timeLine.size(),10));
	        for (Tweet t:subList){
	        	res.add(t.id);
	        }
	        return res;
	        
	    }
	    
	    /** Follower follows a followee. If the operation is invalid, it should be a no-op. */
	    public void follow(int followerId, int followeeId) {
	        if(!userMap.containsKey(followerId)){
	            User u = new User(followerId);
	            userMap.put(followerId, u);
	        }
	        if(!userMap.containsKey(followeeId)){
	            User u = new User(followeeId);
	            userMap.put(followeeId, u);
	        }
	        userMap.get(followerId).follow(followeeId);
	    }
	    
	    /** Follower unfollows a followee. If the operation is invalid, it should be a no-op. */
	    public void unfollow(int followerId, int followeeId) {
	        if(!userMap.containsKey(followerId) || followerId==followeeId|| !userMap.containsKey(followeeId))
	            return;
	        userMap.get(followerId).unfollow(followeeId);
	    }
    
    public static void main(String[] a) {
    	MemoryTwitter tweeter = new MemoryTwitter();
    	tweeter.postTweet(1, 5);
    	tweeter.postTweet(1, 3);
    	tweeter.postTweet(1, 101);
    	tweeter.postTweet(1, 13);
    	tweeter.postTweet(1, 2);
    	tweeter.postTweet(1, 94);
    	tweeter.postTweet(1, 505);
    	tweeter.postTweet(1, 333);
    	tweeter.postTweet(1, 22);
    	tweeter.postTweet(1, 11);
    	List<Integer> result = tweeter.getNewsFeed(1);
    	System.out.println("Found "+result.size()+" results");
    	System.out.println("DONE");
    }
}