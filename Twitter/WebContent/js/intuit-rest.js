var currentUser;
var feedAPI = "twitter/tweets";
var followeeAPI = "twitter/followees";
var unfollowAPI = "twitter/unfollow";
var followAPI = "twitter/follow";
var searchAPI = "twitter/search";
var logoutAPI ="twitter/logout";
var userMessagesAPI ="twitter/tweets/user/";
var feedPage = 1;
var timelinePage = 1;
  
$(document).ready(function(){
  getTweets(true);
  getFollowees(); 
});

function getTweets(searchAfter){
  $.ajax({
	type: "GET",
	url: feedAPI,
	data: { 
	    page: feedPage
	  },
    dataType: "json",
    success: function(json, status){
    	$("#tweets tr").remove();
    	feedPage = json.page;
    	var totalPages = json.totalPages;
    	currentUser = json.user;
    	$("#currentUser").html(currentUser);
    	if (totalPages>1){
    		$("#feedPage").html(" Page "+feedPage +" of "+totalPages+" ");
    	}
    	else{
    		$("#feedPage").html("");
    	}
    	if (feedPage>1){
    		$("#feedPageBack").html("<a href='javascript:void(0)' onclick='getTweetsBack()'>Previous </a>");
    	}
    	else{
    		$("#feedPageBack").html("");
    	}
    	if (feedPage<totalPages){
    		$("#feedPageNext").html("<a  href='javascript:void(0)' onclick='getTweetsNext()'> Next</a>");
    	}
    	else{
    		$("#feedPageNext").html("");
    	}
   		if (json.Tweets){
	  		$.each(json.Tweets, function (i, n){
	  			var item = json.Tweets[i];
	  			var userName = $("<div>").text(item.username).html(); //anti-xss
	  			var message = $("<div>").text(item.message).html(); //anti-xss
	  			var tweetStr = '<tr><td><a href="javascript:void(0)" onclick="getTweetsForUser(\''+userName+'\',true)">'
		  			+userName+'</a></td><td><div style="min-height: 30px; min-width: 100px; max-width: 140px">'+
	  			message+'</div></td><td>'+item.date+'</td>';
	  			if (userName == currentUser){
	  				tweetStr = tweetStr+
	  				'<td style="width:24px; text-align:center"><a href="javascript:void(0)" onClick="deleteTweet('+item.id+')"><img src="images/trashcan_small.jpg"/></a></td></tr>';
	  			}
	  			else{
	  				tweetStr = tweetStr+"<td/></tr>";
	  			}
	  			$(tweetStr).appendTo('#tweets');
	  		});
	  	}
	  	else{
	  		alert('nothing found in news feed response');
	  	}
	  	if (searchAfter){
	  		search();
	  		getTweetsForUser(currentUser, true);
	  	}
    },
    error: function(jqXHR, textStatus, errorThrown) {
    	if (jqXHR.status == '401'){
    		window.location = 'login.html';
	    		return;
	    	}
	        alert(errorThrown);
	    }
	}); 	
}
 function getTweetsBack(){
  	 feedPage = feedPage - 1;
  	 getTweets();
  }
  function getTweetsNext(){
  	 feedPage = feedPage + 1;
  	 getTweets();
  }
  
  function getUserTweetsBack(userId){
  	 timelinePage = timelinePage - 1;
  	 getTweetsForUser(userId);
  }
  function getUserTweetsNext(userId){
  	 timelinePage = timelinePage + 1;
  	 getTweetsForUser(userId);
  }
  
  function search(searchText){
  	var searchString;
  	if (searchText){
  		searchString = searchText;
  	}
  	else{
  		searchString = '';
  	} 
	$.ajax({
    	type: "GET",
    	url: searchAPI,
    	data: { 
		    query: searchString
		  },
	    dataType: "json",
	    success: function(json){
	    	$("#users tr").remove();
	   		if (json.UserInfos){
		  		$.each(json.UserInfos, function (i, n){
		  			var item = json.UserInfos[i];
		  			var itemUsername = $("<div>").text(item.username).html(); //anti-xss
		  			if (itemUsername == currentUser){
		  				$('<tr><td><a href="javascript:void(0)" onClick="getTweetsForUser(\''+itemUsername+'\',true)">'
			  			+itemUsername+'</a></td><td></td></tr>').appendTo('#users');
		  			}
		  			else{
			  			$('<tr><td><a  href="javascript:void(0)" onClick="getTweetsForUser(\''+itemUsername+'\',true)">'
			  			+itemUsername+'</a></td><td style="text-align:center"><input id="followuser_"'+
			  			itemUsername+' type="checkbox" value="'+itemUsername+'"></td></tr>').appendTo('#users');
			  		}
		  		});
		  	}
		  	else{
		  		alert('nothing found in get users request');
		  	}
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	if (jqXHR.status == '401'){
	    		window.location = 'login.html';
	    		return;
	    	}
	        alert(errorThrown);
	    }
	}); 	
  }
  
   function getTweetsForUser(userId, reset){
  	  if (reset){
  	  	timelinePage = 1;
  	  }
	  $.ajax({
    	type: "GET",
    	url: userMessagesAPI+userId,
	    dataType: "json",
	    data: { 
		    page: timelinePage
		},
	    success: function(json, status){
	    	$("#tweetsforuser tr").remove();
	    	var itemUsername = $("<div>").text(userId).html(); //anti-xss
	    	$("#tweetsforuser_username").html(itemUsername);
	    	timelinePage = json.page;
	    	var totalTimelinePages = json.totalPages;
	    	if (totalTimelinePages>1){
	    		$("#timelinePage").html(" Page "+timelinePage +" of "+totalTimelinePages+" ");
	    	}
	    	else{
	    		$("#timelinePage").html("");
	    	}
	    	if (timelinePage>1){
	    		$("#timelinePageBack").html("<a href='javascript:void(0)' onclick=\"getUserTweetsBack('"+itemUsername+"')\">Previous </a>");
	    	}
	    	else{
	    		$("#timelinePageBack").html("");
	    	}
	    	if (timelinePage<totalTimelinePages){
	    		$("#timelinePageNext").html("<a  href='javascript:void(0)' onclick=\"getUserTweetsNext('"+itemUsername+"')\"> Next</a>");
	    	}
	    	else{
	    		$("#timelinePageNext").html("");
	    	}
	   		if (json.Tweets){
		  		$.each(json.Tweets, function (i, n){
		  			var item = json.Tweets[i];
		  			var itemMessage = $("<div>").text(item.message).html(); //anti-xss
		  			var tweetStr = '<tr><td><div style="min-height: 30px;  min-width: 100px; max-width: 140px">'
		  			+itemMessage+'</div></td><td>'+item.date+'</td>';
		  			
		  			if (item.username == currentUser){
		  				tweetStr = tweetStr+
		  				'<td style="width:24px; text-align:center"><a href="javascript:void(0)" onClick="deleteTweet(\''+item.id+'\',true)"><img src="images/trashcan_small.jpg"/></a></td></tr>';
		  			}
		  			else{
		  				tweetStr = tweetStr+"<td/></tr>";
		  			}
		  			$(tweetStr).appendTo('#tweetsforuser');
		  		});
		  	}
		  	else{
		  		alert('nothing found in user timeline response');
		  	}
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	if (jqXHR.status == '401'){
	    		window.location = 'login.html';
	    		return;
	    	}
	        alert(errorThrown);
	    }
	}); 	
  }
 
  
  function getFollowees(){
	  $.ajax({
    	type: "GET",
    	url: followeeAPI,
	    dataType: "json",
	    success: function(json){
	    	$("#followees tr").remove();
	   		if (json.UserInfos){
		  		$.each(json.UserInfos, function (i, n){
		  			var item = json.UserInfos[i];
		  			var itemUsername = $("<div>").text(item.username).html(); //anti-xss
		  			$('<tr><td><a href="javascript:void(0)" onclick="getTweetsForUser(\''+itemUsername+'\',true)">'
		  			+itemUsername+'</a></td><td style="text-align:center"><input id="unfollow_"'+itemUsername+' type="checkbox" value="'+itemUsername+'"></td>').appendTo('#followees');
		  		});
		  	}
		  	else{
		  		alert('nothing found in followees requests');
		  	}
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	if (jqXHR.status == '401'){
	    		window.location = 'login.html';
	    		return;
	    	}
	        alert(errorThrown);
	    }
	}); 	
  }
  
  
  
  function postTweet(){
  	var tweet = postTxtArea.state.text;
  	if (tweet == ''){
  		alert('content required');
  		return;
  	}
  	$.ajax({
    	type: "POST",
    	url: feedAPI,
	    // The key needs to match your method's input parameter (case-sensitive).
	    data: tweet,
	    contentType: "text/plain",
	    success: function(data){
	    	postTxtArea.setState({text:''});
	    	getTweets();  //refresh
	    	if (document.getElementById('tweetsforuser_username').innerHTML == currentUser){
	    		getTweetsForUser(currentUser);
	    	}
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	if (jqXHR.status == '401'){
	    		window.location = 'login.html';
	    		return;
	    	}
	        alert(errorThrown);
	    }
	}); 	
  }
  function unfollow(){
  	var users = [];
  	$("input[id*='unfollow_']" ).each(function(i, el) {
  		if (el.checked){
  			var user = {"username" : el.value};
	    	users.push(user);
	   }
	});
	if (users.length>0){
		var userInfos = {"UserInfos":users};
		var usersJson = JSON.stringify(userInfos);
		$.ajax({
    	type: "PUT",
    	url: unfollowAPI,
    	data: usersJson,
    	contentType: "application/json",
	    success: function(json){
	    	feedPage = 1;
	    	getTweets();
	    	getFollowees();
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	if (jqXHR.status == '401'){
	    		window.location = 'login.html';
	    		return;
	    	}
	        alert(errorThrown);
	    }
	}); 	
	}
  }
  function follow(){
  	var users = [];
  	$("input[id*='followuser_']" ).each(function(i, el) {
  		if (el.checked){
  			var user = {"username" : el.value};
	    	users.push(user);
	   }
	});
	if (users.length>0){
		var userInfos = {"UserInfos":users};
		var usersJson = JSON.stringify(userInfos);
		$.ajax({
    	type: "PUT",
    	url: followAPI,
    	data: usersJson,
    	contentType: "application/json",
	    success: function(json){
	    	$("input[id*='followuser_']" ).each(function(i, el) {
		  		if (el.checked){
		  			el.checked = false;
			   }
			});
	    	getTweets();
	    	getFollowees();
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	if (jqXHR.status == '401'){
	    		window.location = 'login.html';
	    		return;
	    	}
	        alert(errorThrown);
	    }
	}); 	
	}
  }
  function deleteTweet(id, refreshUserTweets, owner){
  	$.ajax({
    	type: "DELETE",
    	url: feedAPI+"/"+id,
	    // The key needs to match your method's input parameter (case-sensitive).
	    success: function(data){
	    	getTweets();  //refresh
	    	if (refreshUserTweets || document.getElementById('tweetsforuser_username').innerHTML == currentUser){
	    		getTweetsForUser(currentUser);
	    	}
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	if (jqXHR.status == '401'){
	    		return;  //silently ignore.
	    	}
	        alert(errorThrown);
	    }
	}); 	
  }
  function logout(){
  	$.ajax({
    	type: "POST",
    	url: logoutAPI,
	    // The key needs to match your method's input parameter (case-sensitive).
	    success: function(data){
	    	window.location = 'login.html';
	    },
	    error: function(jqXHR, textStatus, errorThrown) {
	    	if (jqXHR.status == '401'){
	    		window.location = 'login.html';
	    		return;
	    	}
	        alert(errorThrown);
	    }
	}); 	
  }

  
