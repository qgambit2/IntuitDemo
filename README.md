# IntuitDemo

#### Twitter is a root folder of Eclipse project that builds a WAR file (<b>intuit.war</b>). See build/build.xml file inside the project.
#### The WAR file can be deployed on J2EE server like Tomcat.
#### The web application requires LDAP server, and Redis to be available.
#### LDAP is configured by specifying <b><i>LDAP_URL</i></b> property. Either Java system property or JNDI (context.xml on tomcat). Also <b><i>LDAP_PRINCIPAL_TEMPLATE</i></b> property (System or JNDI) can be used to specify LDAP user search base. If one is not provided the default <b>cn={0},dc=example,dc=org</b> will be used.  Value inside <b>{0}</b> is the ID used to login into the application.
#### Redis is configured by specifying <b><i>REDIS_HOST</i></b> property. Either  Java system property or JNDI (context.xml on tomcat).  Also <b><i>REDIS_PORT</i></b> property (System or JNDI) can be used to specify Redis port. If one is not provided the default <b>6379</b> will be used. 
#### Properties defined in Tomcat's conf/context.xml:
     <Environment name="REDIS_HOST" value="192.168.99.100" type="java.lang.String"/> 	
     <Environment name="LDAP_URL" value="ldap://192.168.99.100:389"  type="java.lang.String"/>
     <Environment name="LDAP_PRINCIPAL_TEMPLATE" value="cn={0},dc=example,dc=org"  type="java.lang.String"/>
#### Once the web application is up and running it can be accessed by going to root location. Either based on war file name of based on a location specified specifically for it (Server.xml in Tomcat).  For example <i>http://localhost:8080/intuit</i>. Users will only be able to login using valid credential of users from LDAP.  For REST documentation the relative url is <i>twitter</i>. For example  <i>http://localhost:8080/intuit/twitter</i>

