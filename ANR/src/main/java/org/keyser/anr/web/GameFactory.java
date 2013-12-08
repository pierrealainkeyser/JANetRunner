package org.keyser.anr.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.codehaus.jackson.map.ObjectMapper;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.OCTGNParser;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.runner.Runner;

public class GameFactory {

	private GameDTOBuilder builder;

	private ObjectMapper mapper;

	// XXX on pourra placer une strategie pour lire d'autr eformat, en se basant
	// sur une propriete de DeckResource qui précisera le format
	private OCTGNParser parser = new OCTGNParser();

	public GameAsDTOGateway create(GameDef def) {

		// new FileInputStream(new File("src/test/resources/core-nbn.o8d"))

		Corp c = null;
		try (InputStream fis = def.getDeckCorp().openStream()) {
			c = parser.parseCorp(fis);
		} catch (IOException e) {
			// TODO faire mieux...
			return null;
		}
		// new FileInputStream(new File("src/test/resources/core-shapper.o8d"))

		Runner r = null;
		try (InputStream fis = def.getDeckRunner().openStream()) {
			r = parser.parseRunner(fis);
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

		g.getCorp().draw(() -> {
		});
		g.getCorp().draw(() -> {
		});
		g.getCorp().draw(() -> {
		});
		g.getCorp().draw(() -> {
		});
		g.getCorp().draw(() -> {
		});

		g.start();

		// renvoi le DTO
		return new GameAsDTOGateway(g, builder, mapper);
	}

	public void setBuilder(GameDTOBuilder builder) {
		this.builder = builder;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}
}
