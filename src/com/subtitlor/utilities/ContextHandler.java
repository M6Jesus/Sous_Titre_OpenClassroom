package com.subtitlor.utilities;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextHandler implements ServletContextListener {
	private static ServletContext context;
    
	public static ServletContext getContext() {
		return context;
	}
	
	// This method is called when application is initialized
	public void contextInitialized(ServletContextEvent event) {
		context = event.getServletContext();
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		// Do here some operation when application is closed.
	}
}
