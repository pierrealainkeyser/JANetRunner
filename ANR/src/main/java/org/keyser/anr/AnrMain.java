package org.keyser.anr;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.keyser.anr.core.ANRMetaCards;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.GameDef;
import org.keyser.anr.core.OCTGNParser;
import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.TestOCTGNParser;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.UserInputConverter;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.web.AnrUserInputConverter;
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
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(basePackageClasses = { GameController.class })
@EnableAutoConfiguration
@EnableWebSocket
public class AnrMain extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(AnrMain.class, args);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(anrWebSocketHandler(), "/ws/play");
	}

	@Bean
	public UserInputConverter userInputConverter() {
		return new AnrUserInputConverter();
	}

	@Bean
	public AnrWebSocketHandler anrWebSocketHandler() {
		return new AnrWebSocketHandler(objectMapper(), userInputConverter(), gameRepository());
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		return objectMapper;
	}

	@Bean
	public OCTGNParser oCTGNParser() {
		return new OCTGNParser();
	}

	@Bean
	public GameRepository gameRepository() {
		GameRepository gameRepository = new GameRepository();

		Game g = new Game();

		GameDef def = new GameDef();
		OCTGNParser p = new OCTGNParser();
		try (InputStream fis = TestOCTGNParser.class.getResourceAsStream("/core-shapper.o8d")) {
			def.setRunner(p.parseRunner(fis));
		} catch (IOException e) {

		}
		try (InputStream fis = TestOCTGNParser.class.getResourceAsStream("/core-nbn.o8d")) {
			def.setCorp(p.parseCorp(fis));
		} catch (IOException e) {
		}

		g.load(def, ANRMetaCards.INSTANCE);

		Representer r = new Representer() {

			@Override
			protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
				if (propertyValue == null) {
					return null;
				} else {
					return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
				}
			}
		};

		Corp corp = g.getCorp();
		corp.setToken(TokenType.CREDIT, 5);
		corp.draw(1, () -> {
		});

		Ice i = (Ice) corp.getHq().getStack().get(0);
		corp.getRd().addIce(i, 0);
		i.setInstalled(true);

		Runner runner = g.getRunner();
		runner.setToken(TokenType.CREDIT, 10);
		runner.draw(5, () -> {
		});

		GameDef gd = g.createDef();

		Yaml yaml = new Yaml(r);
		String dump = yaml.dump(gd);
		System.out.println(dump);

		g.startWith(PlayerType.RUNNER, 1);

		gameRepository.register(new Endpoint(endpointProcessor(), g, "123", "456"));

		return gameRepository;
	}
	
	@Bean
	public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
		return new ResourceUrlEncodingFilter();
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		VersionResourceResolver versionResourceResolver = new VersionResourceResolver().addVersionStrategy(new ContentVersionStrategy(), "/**");
		int cachePeriod = (int) TimeUnit.DAYS.toSeconds(365);

		if (!registry.hasMappingForPattern("/**")) {
			registry.addResourceHandler("/**")//
					.addResourceLocations("classpath:/static/")//
					.setCachePeriod(cachePeriod) //
					.resourceChain(true).addResolver(versionResourceResolver);
		}
		
		if (!registry.hasMappingForPattern("/webjars/**")) {
			registry.addResourceHandler("/webjars/**")
					.addResourceLocations("classpath:/META-INF/resources/webjars/")
					.setCachePeriod(cachePeriod);
		}
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