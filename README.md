# IntuitDemo

#### Eclipse project to construct WAR file.
#### The WAR file can be deployed on J2EE server like Tomcat.
#### The web application requires LDAP server, and Redis to be available.
#### LDAP is configured by specifying <b><i>LDAP_URL</i></b> property. Either Java system property or JNDI (Context.xml on tomcat). Also <b><i>LDAP_PRINCIPAL_TEMPLATE</i></b> property (System or JNDI) can be used to specify LDAP user search base. If one is not provided the default <b>cn={0},dc=example,dc=org</b> will be used.  Value inside <b>{0}</b> is the ID used to login into the application.
#### Redis is configured by specifying <b><i>REDIS_HOST</i></b> property. Either  Java system property or JNDI (Context.xml on tomcat).  Also <b><i>REDIS_PORT</i></b> property (System or JNDI) can be used to specify Redis port. If one is not provided the default <b>6379</b> will be used. 
