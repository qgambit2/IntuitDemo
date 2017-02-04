package intuit_interview.rest;

import java.net.URI;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import intuit_interview.impl.TwitterRedisImpl;
import intuit_interview.interfaces.Twitter;
import intuit_interview.interfaces.TwitterService;
import intuit_interview.model.AuthenticationException;
import intuit_interview.model.Timeline;
import intuit_interview.model.Tweet;
import intuit_interview.model.TwitterException;
import intuit_interview.model.UserNotFoundException;
import intuit_interview.model.Users;

public class TwitterServiceImpl implements TwitterService {

	Twitter twitter = new TwitterRedisImpl();
	private static final Logger logger                  =
             LoggerFactory.getLogger(TwitterServiceImpl.class);
	
	public Timeline getNewsFeed(int page, Cookie authToken) {
		try{
			String username = validateToken(authToken);
			return twitter.getNewsFeed(username, page);
		} catch(WebApplicationException e){
			throw e;
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);	
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	public Timeline getTimeline(String username, int page, Cookie authToken){
		try{
			validateToken(authToken);
			return twitter.getTimeline(username, page);
		} catch(WebApplicationException e){
			throw e;
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);	
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}


	public Response postTweet(String message, Cookie authToken, UriInfo uris) {
		try{
			String username = validateToken(authToken);
			Tweet tweet = new Tweet();
			tweet.setUsername(username);
			tweet.setMessage(message.length()>140?message.substring(0, 140): message);
			long tweetId = twitter.postTweet(tweet);
			tweet.setId(tweetId);
			UriBuilder builder = uris.getBaseUriBuilder();
			URI self = builder.path(TwitterService.class).path(TwitterService.class, "getTweet").build(tweetId);
			return Response.created(self).entity(tweet).build();
		} catch(WebApplicationException e){
			throw e;
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);		
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	public Tweet getTweet(long tweetId,  Cookie authToken){
		try{
			validateToken(authToken);
			Tweet t = twitter.getTweet(tweetId);
			if (t != null){
				return t;
			}
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch(WebApplicationException e){
			throw e;
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);	
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	public Response deleteTweet(long tweetId, Cookie authToken, UriInfo uris) {
		try{
			String username = validateToken(authToken);
			Tweet tweet = twitter.getTweet(tweetId);
			if (tweet == null){
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			String owner = tweet.getUsername();
			if (!username.equals(owner)){
				throw new WebApplicationException(Status.UNAUTHORIZED);
			}
			twitter.removeTweet(tweetId);
			UriBuilder builder = uris.getBaseUriBuilder();
			URI userTimeline = builder.path(TwitterService.class).path(TwitterService.class, "getTimeline").build(username,
					1);
			return Response.noContent().contentLocation(userTimeline).build();
		} catch(WebApplicationException e){
			throw e;
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);		
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
		
	}

	public Response follow(Users users, Cookie authToken,  UriInfo uris) {
		try{
			String username = validateToken(authToken);
			twitter.follow(username, users);
			
			UriBuilder builder = uris.getBaseUriBuilder();
			URI followees = builder.path(TwitterService.class).path(TwitterService.class, "getFollowees").build();
			return Response.ok().contentLocation(followees).build();
		} catch(WebApplicationException e){
			throw e;
		} catch(UserNotFoundException e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);	
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);		
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response unfollow(Users users, Cookie authToken, UriInfo uris) {
		try{
			String username = validateToken(authToken);
			twitter.unfollow(username, users);
			
			UriBuilder builder = uris.getBaseUriBuilder();
			URI followees = builder.path(TwitterService.class).path(TwitterService.class, "getFollowees").build();
			return Response.ok().contentLocation(followees).build();
		} catch(WebApplicationException e){
			throw e;
		} catch(UserNotFoundException e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);	
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);	
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	public Users getFollowees(Cookie authToken) {
		try{
			String username = validateToken(authToken);
			return twitter.getFollowees(username);
		} catch(WebApplicationException e){
			throw e;
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);	
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Users findUsers(String search, Cookie authToken) {
		try{
			validateToken(authToken);
			return this.twitter.findUsers(search);
		} catch(WebApplicationException e){
			throw e;
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);	
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
		
	}

	public Response login(String userName, String password) {
		try{
			String cookieValue = this.twitter.login(userName, password);
			return Response.ok().cookie(new NewCookie("ITD_AuthToken",
					cookieValue)).build();
		} catch(AuthenticationException e){
			throw new WebApplicationException(Status.UNAUTHORIZED);
		} catch(UserNotFoundException e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Response.Status.BAD_REQUEST);	
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);	
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response logout(Cookie authToken) {
		try{
			if (authToken==null){
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
			String username = twitter.getUserForToken(authToken.getValue());
			if (username!=null){
				this.twitter.logout(username);
			}
			return Response.ok().header(
				    "Set-Cookie",
				    "ITD_AuthToken=deleted;Expires=Thu, 01-Jan-1970 00:00:01 GMT"
				 ).build();
		} catch(TwitterException e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);	
		} catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String validateToken(Cookie authToken) throws TwitterException{
		if (authToken==null){
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		String username = twitter.getUserForToken(authToken.getValue());
		if (username == null){
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		return username;
	}

	public Response getDocumentation() {
		try{
			URI location = UriBuilder.fromPath("../swagger-TwitterService.html").build();
			return Response.temporaryRedirect(location).build();
		}
		catch(Throwable e){
			logger.error(e.getMessage(), e);
			Response r = Response.serverError().entity(e.getMessage()).build();
			throw new WebApplicationException(r);
		}
	}

}
