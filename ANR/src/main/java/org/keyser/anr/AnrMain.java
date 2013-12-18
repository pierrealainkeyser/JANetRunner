package org.keyser.anr;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

public class AnrMain {
	public static void main(String[] args) throws Exception {

		Server s = new Server(8082);

		WebAppContext context = new WebAppContext();
		context.setBaseResource(Resource.newClassPathResource("/"));
		context.setContextPath("/");
		
		s.setHandler(context);

		s.start();
		s.join();
	}

}
