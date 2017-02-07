# Twitter Clone Case Study

#### Twitter is a root folder of Eclipse project that builds a WAR file (<b>intuit.war</b>). See build/build.xml file inside the project.
#### The WAR file can be deployed on J2EE server like Tomcat.
#### The web application requires LDAP server, and Redis to be available.
#### LDAP is configured by specifying <b><i>LDAP_URL</i></b> property. Either Java system property or JNDI (context.xml on tomcat). Also <b><i>LDAP_PRINCIPAL_TEMPLATE</i></b> property (System or JNDI) can be used to specify LDAP user search base. If one is not provided the default <b>cn={0},dc=example,dc=org</b> will be used.  Value inside <b>{0}</b> is the ID used to login into the application.
#### Redis is configured by specifying <b><i>REDIS_HOST</i></b> property. Either  Java system property or JNDI (context.xml on tomcat).  Also <b><i>REDIS_PORT</i></b> property (System or JNDI) can be used to specify Redis port. If one is not provided the default <b>6379</b> will be used. 
#### Sample environment properties defined in Tomcat's conf/context.xml:
     <Environment name="TWITTER_TYPE" value="REDIS"  type="java.lang.String"/>  <!-- MEMORY or REDIS -->
     <Environment name="REDIS_HOST" value="192.168.99.100" type="java.lang.String"/> 	
     <Environment name="IDENTITY_SOURCE_TYPE" value="LDAP" type="java.lang.String"/> <!-- LDAP or DUMMY -->	
     <Environment name="LDAP_URL" value="ldap://192.168.99.100:389"  type="java.lang.String"/>
     <Environment name="LDAP_PRINCIPAL_TEMPLATE" value="cn={0},dc=example,dc=org"  type="java.lang.String"/>
     <Environment name="PAGE_SIZE" value="100"  type="java.lang.String"/>	
     <Environment name="INACTIVITY_TIMEOUT" value="1800"  type="java.lang.String"/>	
     <Environment name="MAXIMUM_TIMELINE_SIZE" value="1000"  type="java.lang.String"/>
     
#### Once the web application is up and running it can be accessed by going to root location. Either based on war file name of based on a location specified specifically for it (Server.xml in Tomcat).  For example <i>https://52.52.230.64:8443/</i>. Users will only be able to login using valid credential of users from LDAP.  For REST documentation the relative url is <i>twitter</i>. For example  <i>https://52.52.230.64:8443/twitter</i>

## AWS Demo

### Working example is provided on AWS. It consists of:
1. Ubuntu EC2 instance with Tomcat installed. Application is deployed on Tomcat.  Location is https://52.52.230.64:8443/
2. Amazon Linux EC2 instance on which Docker is installed. Open LDAP, and LDAP admin UI containers are hosted on Docker.  LDAP admin UI is https://52.8.60.149:6443/
3. Redis from Amazon's ElastiCache service.

