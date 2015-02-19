package org.keyser.anr;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.keyser.anr.core.Game;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.corp.nbn.MakingNews;
import org.keyser.anr.core.runner.shaper.KateMcCaffrey;
import org.keyser.anr.web.AnrWebSocketHandler;
import org.keyser.anr.web.Endpoint;
import org.keyser.anr.web.EndpointProcessor;
import org.keyser.anr.web.GameController;
import org.keyser.anr.web.GameRepository;
import org.keyser.anr.web.MailboxEndpointProcessor;
import org.keyser.anr.web.RemoteCardLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(basePackageClasses = { GameController.class })
@EnableAutoConfiguration
@EnableWebSocket
public class AnrMain implements WebSocketConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(AnrMain.class, args);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(anrWebSocketHandler(), "/ws/play");
	}

	@Bean
	public AnrWebSocketHandler anrWebSocketHandler() {
		return new AnrWebSocketHandler(objectMapper(), null, gameRepository());
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();		
		objectMapper.setSerializationInclusion(Include.NON_NULL);		
		return objectMapper;
	}

	@Bean
	public GameRepository gameRepository() {
		GameRepository gameRepository = new GameRepository();

		Game g = new Game();
		MakingNews mn = (MakingNews) g.create(MakingNews.INSTANCE);
		mn.setToken(TokenType.CREDIT, 5);
		
		KateMcCaffrey kcc = (KateMcCaffrey) g.create(KateMcCaffrey.INSTANCE);
		kcc.setToken(TokenType.CREDIT, 5);
		g.start();

		gameRepository.register(new Endpoint(endpointProcessor(), g, "123", "456"));

		return gameRepository;
	}

	@Bean
	public EndpointProcessor endpointProcessor() {
		return new MailboxEndpointProcessor(executor());
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