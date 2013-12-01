package org.keyser.anr;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class AnrMain {
	public static void main(String[] args) throws Exception {

		Server s = new Server(8082);

		WebAppContext context = new WebAppContext();
		context.setResourceBase("src/main/resources/WebContent");
		context.setContextPath("/");
		
		s.setHandler(context);

		s.start();
		s.join();
	}

}
