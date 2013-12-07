package org.keyser.anr.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.function.Function;

import org.codehaus.jackson.map.ObjectMapper;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.OCTGNParser;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.runner.Runner;
import org.springframework.beans.factory.FactoryBean;

public class GameBootstrap implements FactoryBean<Function<String, GameGateway>> {

	private GameDTOBuilder builder;

	private ObjectMapper mapper;

	public void setBuilder(GameDTOBuilder builder) {
		this.builder = builder;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public Function<String, GameGateway> getObject() throws Exception {
		// TODO renvoyer un vrai objet réutilisable, qui pointe vers un
		// GameAsDTOGateway
		return (s) -> new GameAsDTOGateway(create(), builder, mapper);
	}

	/**
	 * C'est du faux, il faut une map de ID >> {@link GameAsDTOGateway}
	 * 
	 * @param name
	 * @return
	 */
	private Game create() {
		OCTGNParser p = new OCTGNParser();

		Corp c = null;
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/core-nbn.o8d"))) {
			c = p.parseCorp(fis);
		} catch (IOException e) {
			// TODO faire mieux...
			return null;
		}
		
		Runner r = null;
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/core-shapper.o8d"))) {
			r = p.parseRunner(fis);
		} catch (IOException e) {
			// TODO faire mieux...
			return null;
		}

		Game g = new Game(r, c, () -> {
		}).setup();
		
		Collections.shuffle(r.getStack());
		Collections.shuffle(c.getStack());

		Wallet w = c.getWallet();
		w.wallet(WalletCredits.class, wu -> wu.setAmount(5));

		w = r.getWallet();
		w.wallet(WalletCredits.class, wu -> wu.setAmount(5));
		
		g.getCorp().draw(()->{});
		g.getCorp().draw(()->{});
		g.getCorp().draw(()->{});
		g.getCorp().draw(()->{});
		g.getCorp().draw(()->{});
		
		g.start();

		return g;
	}

	@Override
	public Class<?> getObjectType() {
		return Function.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
