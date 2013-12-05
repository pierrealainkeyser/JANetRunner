package org.keyser.anr.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Function;

import org.codehaus.jackson.map.ObjectMapper;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.OCTGNParser;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.WalletActions;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.runner.shapper.KateMcCaffrey;
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
		return (s) -> new GameAsDTOGateway(lookup(s), builder, mapper);
	}

	/**
	 * C'est du faux, il faut une map de ID >> {@link GameAsDTOGateway}
	 * 
	 * @param name
	 * @return
	 */
	private Game lookup(String name) {
		OCTGNParser p = new OCTGNParser();

		Corp c = null;
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/core-nbn.o8d"))) {
			c = p.parseCorp(fis);
		} catch (IOException e) {

			// TODO faire mieux...
			return null;
		}

		Game g = new Game(new KateMcCaffrey(), c, () -> {
		}).setup();

		Wallet w = g.getCorp().getWallet();
		w.wallet(WalletCredits.class, wu -> wu.setAmount(5));

		w = g.getRunner().getWallet();
		w.wallet(WalletCredits.class, wu -> wu.setAmount(5));
		
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
