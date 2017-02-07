$(document).ready(function(){
	$("#login").click(function(){
		var username = $("#username").val();
		var password = $("#password").val();
		// Checking for blank fields.
		if( username =='' || password ==''){
			$('input[type="text"],input[type="password"]').css("border","2px solid red");
			$('input[type="text"],input[type="password"]').css("box-shadow","0 0 3px red");
			alert("Please fill all fields...!!!!!!");
		}else {
			var request = $.ajax({
		    	type: "POST",
		    	url: "twitter/login",
		    	data: { username: username, password:password},
			    // The key needs to match your method's input parameter (case-sensitive).
			    success: function(data){
			    	window.location.assign('index.html');
			    },
			    error: function(jqXHR, textStatus, errorThrown) {
				   var status = jqXHR.status;
				   if (status == '401'){
				   		$('input[type="text"],input[type="password"]').css("border","2px solid red");
				   		$('input[type="text"],input[type="password"]').css("box-shadow","0 0 3px red");
				   		$("#error").html("Invalid username and/or password");
				   }
				   else{
				   		$("#error").html("Login failed. See logs for details.");
				   }
			    }
			}); 
		}
	});
});
