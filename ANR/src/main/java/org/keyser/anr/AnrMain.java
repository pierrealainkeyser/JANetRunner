package org.keyser.anr;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.keyser.anr.web.GameController;
import org.keyser.anr.web.RemoteCardLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { GameController.class })
@EnableAutoConfiguration
public class AnrMain {

	public static void main(String[] args) {
		SpringApplication.run(AnrMain.class, args);
	}

	@Bean
	public Executor executor() {
		return Executors.newCachedThreadPool();
	}

	@Bean
	public RemoteCardLoader remoteCardLoader() {
		return new RemoteCardLoader(executor());
	}

}