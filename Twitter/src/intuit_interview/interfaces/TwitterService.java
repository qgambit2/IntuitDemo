package intuit_interview.interfaces;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import intuit_interview.model.Timeline;
import intuit_interview.model.Tweet;
import intuit_interview.model.Users;

@Path("")
public interface TwitterService {

	/**
	 * Returns a Documentation
	 * @return Page containing information on this service
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML})
	public Response getDocumentation();
	
	/**
	 * Returns a tweet feed of the user specified by cookie 
	 * @param page Page number of the feed.
	 * @param authToken Authentication token pointing to logged in user
	 * @return Timeline object containing list of tweets
	 */
	@GET
	@Path("tweets")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Timeline getNewsFeed(@QueryParam("page") @DefaultValue("1") int page,
			@CookieParam("ITD_AuthToken") Cookie authToken);

	/**
	 * Returns a tweet resulting from posting a message
	 * @param message text of the message
	 * @param authToken Authentication token pointing to logged in user
	 * @return Tweet resulting from posting a message
	 */
	@POST
	@Path("tweets")
	@Consumes({MediaType.TEXT_PLAIN})
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response postTweet(String message,
			@CookieParam("ITD_AuthToken") Cookie authToken, @Context UriInfo uris);
	
	
	/**
	 * Returns List of tweets posted by specific users
	 * @param username Username
	 * @param page Page number of the feed.
	 * @param authToken Authentication token pointing to logged in user
	 * @return Timeline object containing list of tweets by the user.
	 */
	@GET
	@Path("tweets/user/{username}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Timeline getTimeline(@PathParam("username") String username, 
			@QueryParam("page") @DefaultValue("1") int page,
			@CookieParam("ITD_AuthToken") Cookie authToken);
	
	/**
	 * Returns a specific tweet
	 * @param tweetId ID of the tweet
	 * @param authToken Authentication token pointing to logged in user
	 * @return Timeline object containing list of tweets by the user.
	 */
	@GET
	@Path("tweets/{tweetId}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Tweet getTweet(@PathParam("tweetId") long tweetId, 
			@CookieParam("ITD_AuthToken") Cookie authToken);
	
	
	/**
	 * Deletes a specific tweet
	 * @param tweetId ID of the tweet
	 * @param authToken Authentication token pointing to logged in user
	 */
	@DELETE
	@Path("tweets/{tweetId}")
	public Response deleteTweet(@PathParam("tweetId") long tweetId, 
			@CookieParam("ITD_AuthToken") Cookie authToken, @Context UriInfo uriInfo);

	/**
	 * Makes logged in user follow another user
	 * @param followeeId Username of a user to follow
	 * @param authToken Authentication token pointing to logged in user
	 */
	@PUT
	@Path("follow")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.TEXT_PLAIN })
	public Response follow(Users followees,
			@CookieParam("ITD_AuthToken") Cookie authToken, @Context UriInfo uris);

	@PUT
	@Path("unfollow")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.TEXT_PLAIN })
	public Response unfollow(Users followees,
			@CookieParam("ITD_AuthToken") Cookie authToken, @Context UriInfo uris);
	

	/**
	 * Returns a list of users logged in user follows
	 * @param authToken Authentication token pointing to logged in user
	 * @return List of users logged in user follows
	 */
	@GET
	@Path("followees")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Users getFollowees(@CookieParam("ITD_AuthToken") 
			Cookie authToken);
	
	/**
	 * Returns a list of users matching search string
	 * @param query Partial username
	 * @param authToken Authentication token pointing to logged in user
	 * @return List of users matching query string
	 */
	@GET
	@Path("search")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Users findUsers(@QueryParam("query") String query,
			@CookieParam("ITD_AuthToken") Cookie authToken);

	/**
	 * Logs in user
	 * @param username Username
	 * @param password Password
	 */
	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.TEXT_PLAIN })
	public Response login(@FormParam("username") String username, 
			@FormParam("password") String password);
	

	/**
	 * Logs the user out
	 * @param authToken Authentication token pointing to logged in user
	 */
	@POST
	@Path("logout")
	@Produces({ MediaType.TEXT_PLAIN })
	public Response logout(@CookieParam("ITD_AuthToken") Cookie authToken);
	
	
}
