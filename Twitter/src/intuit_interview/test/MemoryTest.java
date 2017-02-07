package intuit_interview.test;

import java.util.ArrayList;
import java.util.List;

import intuit_interview.impl.DummyIdentitySource;
import intuit_interview.impl.TwitterMemoryImpl;
import intuit_interview.model.Timeline;
import intuit_interview.model.Tweet;
import intuit_interview.model.User;
import intuit_interview.model.Users;


public class MemoryTest {
	public static void main(String [] args) throws Exception{
		runTest();
	}
		
	public static void runTest() throws Exception{
		
		
		System.setProperty("IDENTITY_SOURCE_CLASS", "intuit_interview.impl.DummyIdentitySource");
		TwitterMemoryImpl twitter = new TwitterMemoryImpl();
	    twitter.setIdentitySource(new DummyIdentitySource());
		
		User u1 = new User();
		u1.setUsername("eugene");
		twitter.login("eugene", "password1");
		
		User u2 = new User();
		u2.setUsername("valerie");
		twitter.login("valerie", "password2");
		
		User u3 = new User();
		u3.setUsername("vadim");
		twitter.login("vadim", "password3");
		
		User u4 = new User();
		u4.setUsername("ethan");
		twitter.login("ethan", "password4");
		
		User u5 = new User();
		u5.setUsername("curious_george");
		twitter.login("curious_george", "password5");

		//everyone (Except George) follows Eugene
		intuit_interview.model.Users eugeneUsers = new intuit_interview.model.Users();
		intuit_interview.model.User eugene = new intuit_interview.model.User();
		eugene.setUsername("eugene");	
		List<User> eugeneList = new ArrayList<User>();
		eugeneList.add(eugene);
		eugeneUsers.setUsers(eugeneList);
		
		
		twitter.follow("valerie", eugeneUsers);
		twitter.follow("vadim", eugeneUsers);
		twitter.follow("ethan", eugeneUsers);
		
		
		intuit_interview.model.Users valerieUsers = new intuit_interview.model.Users();
		intuit_interview.model.User valerie = new intuit_interview.model.User();
		valerie.setUsername("valerie");
		List<User> valerieList = new ArrayList<User>();
		valerieList.add(valerie);
		valerieUsers.setUsers(valerieList);
		
		
		intuit_interview.model.Users ethanUsers = new intuit_interview.model.Users();
		intuit_interview.model.User ethan = new intuit_interview.model.User();
		ethan.setUsername("ethan");
		List<User> ethanList = new ArrayList<User>();
		ethanList.add(ethan);
		ethanUsers.setUsers(ethanList);
		
		
		intuit_interview.model.Users vadimUsers = new intuit_interview.model.Users();
		intuit_interview.model.User vadim = new intuit_interview.model.User();
		vadim.setUsername("vadim");
		List<User> vadimList = new ArrayList<User>();
		vadimList.add(vadim);
		vadimUsers.setUsers(vadimList);

		
		//eugene follows Valerie
		twitter.follow("eugene", valerieUsers);
		
		
		//George follows Ethan
		twitter.follow("curious_george", ethanUsers);
		
		
		List<Tweet> tweets = twitter.getNewsFeed("eugene", 1).getTweets();
		if (tweets.size()!=0){
			throw new Exception("There should be no tweets at this point");
		}
		
		Tweet t1 =new Tweet();
		t1.setMessage("My First tweet");
		t1.setUsername("eugene");
		twitter.postTweet(t1);
		Thread.sleep(300);
		
		tweets = twitter.getNewsFeed("eugene", 1).getTweets();
		if (tweets.size()!=1){
			throw new Exception("There should be one tweet at this point but have "+tweets.size());
		}
		tweets = twitter.getNewsFeed("eugene", 2).getTweets();
		if (tweets.size()!=0){
			throw new Exception("There should be no tweets on second page");
		}
		
		tweets = twitter.getNewsFeed("valerie", 1).getTweets();
		if (tweets.size()!=1){
			throw new Exception("There should be one tweet on first page");
		}
		tweets = twitter.getNewsFeed("vadim", 1).getTweets();
		if (tweets.size()!=1){
			throw new Exception("There should be one tweet on first page");
		}
		tweets = twitter.getNewsFeed("ethan", 1).getTweets();
		if (tweets.size()!=1){
			throw new Exception("There should be no tweets on second page");
		}
		tweets = twitter.getNewsFeed("curious_george", 1).getTweets();
		if (tweets.size()!=0){
			throw new Exception("George should have no posts yet");
		}
		twitter.follow("curious_george", eugeneUsers);
		List<User> georgeFollows2 = twitter.getFollowees("curious_george").getUsers();
		if (georgeFollows2.size()!=2){
			throw new Exception("George should be following 2 persons but is following: "+georgeFollows2.size());
		}
		
		
		tweets = twitter.getNewsFeed("curious_george", 1).getTweets();
		if (tweets.size()!=1){
			throw new Exception("George should have one post now");
		}
		twitter.unfollow("curious_george", eugeneUsers);
		tweets = twitter.getNewsFeed("curious_george", 1).getTweets();
		if (tweets.size()!=0){
			throw new Exception("George should have no posts now");
		}
		
		Tweet ethanFirstTweet = new Tweet();
		ethanFirstTweet.setMessage("Ethan first tweet");
		ethanFirstTweet.setUsername("ethan");
		twitter.postTweet(ethanFirstTweet);
		Thread.sleep(200);
		
		tweets = twitter.getNewsFeed("ethan", 1).getTweets();
		if (tweets.size()!=2){
			throw new Exception("Ehan should have 2  posts now");
		}
		
		List<User> georgeFollows = twitter.getFollowees("curious_george").getUsers();
		if (georgeFollows.size()!=1){
			throw new Exception("George should be following one person but is following: "+georgeFollows.size());
		}
		tweets = twitter.getNewsFeed("curious_george", 1).getTweets();
		if (tweets.size()!=1){
			throw new Exception("George should have 1  posts now but has "+tweets.size());
		}
		
		Tweet valFirstTweet = new Tweet();
		valFirstTweet.setMessage("Valerie first tweet");
		valFirstTweet.setUsername("valerie");
		twitter.postTweet(valFirstTweet);
		Thread.sleep(200);
		
		Tweet valSecondTweet = new Tweet();
		valSecondTweet.setMessage("Valerie Second tweet");
		valSecondTweet.setUsername("valerie");
		twitter.postTweet(valSecondTweet);
		
		Thread.sleep(200);
		
		tweets = twitter.getNewsFeed("eugene", 1).getTweets();
		if (tweets.size()!=3){
			throw new Exception("Eugene should have 3 tweets now");
		}
		for (int i=0;i<tweets.size();i++){
			Tweet tweet = tweets.get(i);
			long tweetId = tweet.getId();
			if (i == 0 && tweetId!=4){
				throw new Exception("Tweet Order is incorrect");
			}
			if (i == 1 && tweetId!=3){
				throw new Exception("Tweet Order is incorrect");
			}
			if (i == 2 && tweetId!=1){
				throw new Exception("Tweet Order is incorrect");
			}
		}
		twitter.unfollow("valerie", eugeneUsers);
		tweets = twitter.getNewsFeed("valerie", 1).getTweets();
		if (tweets.size()!=2){
			throw new Exception("Valerie should have 2 tweets now");
		}
		
		for (int i=0;i<tweets.size();i++){
			Tweet tweet = tweets.get(i);
			long tweetId = tweet.getId();
			if (i == 0 && tweetId!=4){
				throw new Exception("Tweet Order is incorrect");
			}
			if (i == 1 && tweetId!=3){
				throw new Exception("Tweet Order is incorrect");
			}
		}
		
		Tweet eugeneSecondTweet = new Tweet();
		eugeneSecondTweet.setMessage("Eugene second tweet");
		eugeneSecondTweet.setUsername("eugene");
		twitter.postTweet(eugeneSecondTweet);
		Thread.sleep(200);
		
		twitter.follow("valerie", eugeneUsers);
		
		tweets = twitter.getNewsFeed("valerie", 1).getTweets();
		if (tweets.size()!=4){
			throw new Exception("Valerie should have 4 tweets now");
		}
		
		for (int i=0;i<tweets.size();i++){
			Tweet tweet = tweets.get(i);
			long tweetId = tweet.getId();
			if (i == 0 && tweetId!=5){
				throw new Exception("Tweet Order is incorrect");
			}
			if (i == 1 && tweetId!=4){
				throw new Exception("Tweet Order is incorrect");
			}
			if (i == 2 && tweetId!=3){
				throw new Exception("Tweet Order is incorrect");
			}
			if (i == 3 && tweetId!=1){
				throw new Exception("Tweet Order is incorrect");
			}
		}
		
		tweets = twitter.getNewsFeed("curious_george", 1).getTweets();
		if (tweets.size()!=1){
			throw new Exception("George should have 1 post");
		}
		
		twitter.follow("ethan", valerieUsers);
		twitter.follow("ethan", vadimUsers);
		List<User> ethanFollows = twitter.getFollowees("ethan").getUsers();
		if (ethanFollows.size()!=3){
			throw new Exception("Ethan should be following 3");
		}
		twitter.unfollow("ethan", vadimUsers);
		ethanFollows = twitter.getFollowees("ethan").getUsers();
		if (ethanFollows.size()!=2){
			throw new Exception("Ethan should be following 2");
		}
		
		for (int i=0;i<10;i++){
			Tweet t = new Tweet();
			t.setMessage("ok");
			t.setUsername("eugene");
			twitter.postTweet(t);
		}
		for (int i=0;i<9;i++){
			//twitter.removeTweet(i);
		}
		Thread.sleep(200);
		
		Timeline timeline = twitter.getNewsFeed("eugene", 1);
		tweets = timeline.getTweets();
		if (tweets.size()!=14){
			throw new Exception("Eugene should have 14 posts but has "+tweets.size());
		}
		tweets = twitter.getNewsFeed("valerie", 1).getTweets();
		if (tweets.size()!=14){
			throw new Exception("Valerie should have 14 posts but has "+tweets.size());
		}
		tweets = twitter.getNewsFeed("curious_george", 1).getTweets();
		if (tweets.size()!=1){
			throw new Exception("curious_george should have 1 tweets but has "+tweets.size());
		}
		
		Users u = twitter.findUsers("e");
		if (u.getTotal()!=2){
			throw new Exception("Expecting 2 users for 'e' but got: "+u.getTotal());
		}
		if (u.getUsers()==null){
			throw new Exception("UserList not set in users");
		}
		if (u.getUsers().size()!=2){
			throw new Exception("User list should have 2 users but has "+u.getUsers().size());
		}
		
		System.out.println("DONE with ALL tests");
		
		
	}
}
