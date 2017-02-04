package intuit_interview.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;

public class TwitterApplication extends Application {
	public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(TwitterServiceImpl.class);
        return classes;
    }
	
	public Set<Object> getSingletons() {
		Set<Object> s = new HashSet<Object>();
		JacksonJaxbJsonProvider jaxbProvider = new JacksonJaxbJsonProvider();
		s.add(jaxbProvider);
		return s;
	}
}
