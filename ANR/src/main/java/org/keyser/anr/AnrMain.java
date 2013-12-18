package org.keyser.anr;

import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class AnrMain {
	public static void main(String[] args) throws Exception {
		Server s = new Server(8082);

		ProtectionDomain domain = AnrMain.class.getProtectionDomain();
		URL location = domain.getCodeSource().getLocation();

		WebAppContext context = new WebAppContext();
		context.setWar(location.toExternalForm());
		context.setContextPath("/");

		s.setHandler(context);

		s.start();
		s.join();
	}

}
