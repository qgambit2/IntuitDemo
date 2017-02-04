package intuit_interview.interfaces;

import intuit_interview.model.Timeline;
import intuit_interview.model.Tweet;
import intuit_interview.model.TwitterException;
import intuit_interview.model.Users;

public interface Twitter {
	 public long postTweet(Tweet tweet)  throws TwitterException;
	 public Timeline getNewsFeed(String userId, int page) throws TwitterException;
	 public Timeline getTimeline(String userId, int page) throws TwitterException;
	 public void follow(String followerId, Users followees) throws TwitterException;
	 public void unfollow(String followerId, Users followees) throws TwitterException;
	 public Users getFollowees(String username) throws TwitterException;
	 public void removeTweet(long tweetKey) throws TwitterException;
	 public Tweet getTweet(long tweetId) throws TwitterException;
	 public Users findUsers(String search) throws TwitterException;
	 
	 public String login(String username, String password) throws TwitterException;
	 public void logout(String username) throws TwitterException;
	 public String getUserForToken(String token) throws TwitterException;
}
