%%% operation name=getNewsFeed

Gets news feed for individual identified by authentication token cookie.
#### Request:
~~~
GET /twitter/tweets HTTP/1.1
Cookie: ITD_AuthToken=cea1eb81-5c5b-44d7-825b-e6ccfc65da19
Accept: application/json
~~~
#### Responses :
* 200 OK. If successful
* 401 Unauthorized. If authentication token cookie expired or is not provided.

#### Response
~~~
HTTP/1.1 200 OK
Content-Type:application/json

{"Tweets": [
      {
      "id": 19,
      "message": "My Third Tweet",
      "date": "2017-01-28T05:44Z",
      "username": "eugene"
   },
      {
      "id": 18,
      "message": "My Second Tweet",
      "date": "2017-01-28T05:44Z",
      "username": "eugene"
   },
      {
      "id": 17,
      "message": "My First Tweet",
      "date": "2017-01-28T05:43Z",
      "username": "eugene"
   }],
   "page": 1,
   "user": "valerie",
   "totalPages": 1,
   "totalTweets": 3
}
~~~
%%%
%%% operation name=getTimeline

Gets List of posts made by specific individual
#### Request:
~~~
GET /twitter/tweets/username/{id} HTTP/1.1
Cookie:  ITD_AuthToken=cea1eb81-5c5b-44d7-825b-e6ccfc65da19
Accept: application/json
~~~
#### Responses :
* 200 OK. If successful
* 401 Unauthorized. If authorization token cookie expired or is not provided.

#### Response
~~~
HTTP/1.1 200 OK
Content-Type:application/json

{"Tweets": [
      {
      "id": 19,
      "message": "My Third Tweet",
      "date": "2017-01-28T05:44Z",
      "username": "eugene"
   },
      {
      "id": 18,
      "message": "My Second Tweet",
      "date": "2017-01-28T05:44Z",
      "username": "eugene"
   },
      {
      "id": 17,
      "message": "My First Tweet",
      "date": "2017-01-28T05:43Z",
      "username": "eugene"
   }],
   "page": 1,
   "user": "eugene",
   "totalPages": 1,
   "totalTweets": 3
}
~~~
%%%
%%% operation name=postTweet

Post a tweet by individual specified by authentication token cookie
#### Request:
~~~
POST /twitter/tweets/ HTTP/1.1
Accept: application/json
Cookie:  ITD_AuthToken=cea1eb81-5c5b-44d7-825b-e6ccfc65da19
Content-Type: text/plain

This is my message.
~~~
#### Responses :
* 201 Created. If successful
* 401 Unauthorized. If authorization token cookie expired or is not provided.

#### Response
~~~
HTTP/1.1 201 Created
Location: http://localhost:8081/twitter/tweets/20
Content-Type:application/json

{
   "id": 20,
   "message": "My Fourth Tweet",
   "date": "2017-01-29T00:04Z",
   "username": "eugene"
}
~~~
%%%
%%% operation name=getTweet

Gets specific tweet
#### Request:
~~~
GET /twitter/tweets/{id} HTTP/1.1
Cookie: ITD_AuthToken=cea1eb81-5c5b-44d7-825b-e6ccfc65da19
Accept: application/json
~~~
#### Responses :
* 200 OK. If successful
* 401 Unauthorized. If authentication token cookie expired or is not provided.

#### Response
~~~
HTTP/1.1 200 OK
Content-Type:application/json

{
   "id": 20,
   "message": "My Fourth Tweet",
   "date": "2017-01-29T00:04Z",
   "username": "eugene"
}
~~~
%%%
%%% operation name=deleteTweet

Deletes specific tweet
#### Request:
~~~
DELETE /twitter/tweets/{id} HTTP/1.1
Cookie: ITD_AuthToken=cea1eb81-5c5b-44d7-825b-e6ccfc65da19
~~~
#### Responses :
* 204 No Content. If successful
* 401 Unauthorized. If authentication token cookie expired or is not provided.
* 404 Not Found. If tweet with given eid doesn't exist.

#### Response
~~~
HTTP/1.1 204 No Content
Content-Location: http://localhost:8081/twitter/tweets/user/eugene

~~~
%%%
%%% operation name=follow

Follow an individual specified as path parameter. Follower is user identified 
by authentication token cookie.
#### Request:
~~~
PUT /twitter/follow HTTP/1.1
Cookie:  ITD_AuthToken=cea1eb81-5c5b-44d7-825b-e6ccfc65da19
Content-Type: application/json

{
  "UserInfos": [
    {
      "username": "ethan"
    },
    {
      "username": "vadim"
    }
  ]
}
~~~
#### Responses :
* 200 OK. If successful
* 401 Unauthorized. If authorization token cookie expired or is not provided.

#### Response
~~~
HTTP/1.1 200 OK
Content-Location: http://localhost:8081/twitter/followees

~~~
%%%
%%% operation name=unfollow

Unfollow an individual specified as path parameter. Follower is user identified 
by authentication token cookie.
#### Request:
~~~
PUT /twitter/unfollow HTTP/1.1
Cookie:  ITD_AuthToken=cea1eb81-5c5b-44d7-825b-e6ccfc65da19
Content-Type: application/json

{
  "UserInfos": [
    {
      "username": "ethan"
    },
    {
      "username": "vadim"
    }
  ]
}

~~~
#### Responses :
* 200 OK. If successful
* 401 Unauthorized. If authorization token cookie expired or is not provided.

#### Response
~~~
HTTP/1.1 200 OK
Content-Location: http://localhost:8081/twitter/followees

~~~
%%%
%%% operation name=findUsers

Find users based on search string
#### Request:
~~~
GET /twitter/search?query=e HTTP/1.1
Cookie: ITD_AuthToken=cea1eb81-5c5b-44d7-825b-e6ccfc65da19
Accept: application/json
~~~
#### Responses :
* 200 OK. If successful
* 401 Unauthorized. If authentication token cookie expired or is not provided.

#### Response
~~~
HTTP/1.1 200 OK
Content-Type:application/json

{
   "UserInfos":    [
            {
         "username": "ethan",
         "firstName": null,
         "lastName": null
      },
            {
         "username": "eugene",
         "firstName": null,
         "lastName": null
      }
   ],
   "total": 2
}
~~~
%%%
%%% operation name=logout

Logout currently logged in user
#### Request:
~~~
POST /twitter/logout HTTP/1.1
Cookie: ITD_AuthToken=cea1eb81-5c5b-44d7-825b-e6ccfc65da19
~~~
#### Responses :
* 200 OK. If successful

#### Response
~~~
HTTP/1.1 200 OK
Set-Cookie: ITD_AuthToken=deleted;Expires=Thu, 01-Jan-1970 00:00:01 GMT

~~~
%%%
%%% operation name=login

Find users based on search string
#### Request:
~~~
POST /twitter/login HTTP/1.1
Content-Type: application/x-www-form-urlencoded

username=eugene&password=password1
~~~
#### Responses :
* 200 OK. If successful
* 401 Unauthorized. If login fails.

#### Response
~~~
HTTP/1.1 200 OK
Set-Cookie: ITD_AuthToken=e36f006a-8840-4024-bd5b-dd0a2c697732;Version=1

~~~
%%%
%%% operation name=getFollowees

Gets list of followees for individual identified by authentication token cookie.
#### Request:
~~~
GET /twitter/followees HTTP/1.1
Cookie: ITD_AuthToken=cea1eb81-5c5b-44d7-825b-e6ccfc65da19
Accept: application/json
~~~
#### Responses :
* 200 OK. If successful
* 401 Unauthorized. If authentication token cookie expired or is not provided.

#### Response
~~~
HTTP/1.1 200 OK
Content-Type:application/json

{
   "UserInfos":    [
            {
         "username": "ethan",
         "firstName": null,
         "lastName": null
      },
            {
         "username": "vadim",
         "firstName": null,
         "lastName": null
      },
            {
         "username": "valerie",
         "firstName": null,
         "lastName": null
      }
   ],
   "total": 3
}
~~~
%%%
%%% operation name=getDocumentation

Documenation for this API.
#### Request:
~~~
GET /twitter HTTP/1.1

~~~
#### Responses :
* 200 OK. If successful

#### Response
~~~
HTTP/1.1 200 OK
Content-Type:text/html

<html lang="en">
...
</html>
~~~
%%%
