package org.keyser.anr.web.dto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.OCTGNParser;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.runner.Runner;
import org.keyser.anr.web.ConnectedGameEndpoint;
import org.keyser.anr.web.GameAccess;
import org.keyser.anr.web.GameDef;
import org.keyser.anr.web.GameFactory;
import org.keyser.anr.web.GameGateway;

/**
 * Une fabrique de {@link ConnectedGameEndpoint}
 * 
 * @author PAF
 * 
 */
public class GameDTOFactory implements GameFactory {

	private GameDTOBuilder builder = new GameDTOBuilder();

	private ObjectMapper mapper;

	public static enum KeyMode {
		SIMPLE, UNIQUE;

		private String build(GameDef gd, String faction) {
			if (UNIQUE == this)
				return UUID.randomUUID().toString();
			return gd.getKey() + "-" + faction;
		}

	}

	// XXX on pourra placer une strategie pour lire d'autr eformat, en se basant
	// sur une propriete de DeckResource qui précisera le format
	private OCTGNParser parser = new OCTGNParser();

	private KeyMode keyMode = KeyMode.SIMPLE;

	@Override
	public GameAccess createAccess(GameDef def, String faction, GameGateway gateway) {
		return new GameAccess(keyMode.build(def, faction), faction, gateway);
	}

	@Override
	public ConnectedGameEndpoint create(GameDef def) {
		Corp c = null;
		try (InputStream fis = def.getDeckCorp().openStream()) {
			c = parser.parseCorp(fis);
		} catch (IOException e) {
			// TODO faire mieux...
			return null;
		}

		Runner r = null;
		try (InputStream fis = def.getDeckRunner().openStream()) {
			r = parser.parseRunner(fis);
		} catch (IOException e) {
			// TODO faire mieux...
			return null;
		}

		Flow nowhere = () -> {
		};

		//TODO l'initialisation de la partie ne devrait pas se faire ici
		Game g = new Game(r, c, nowhere);
		g.setup();

		Collections.shuffle(r.getStack());
		Collections.shuffle(c.getStack());

		Wallet w = c.getWallet();
		w.wallet(WalletCredits.class, wu -> wu.setAmount(5));

		w = r.getWallet();
		w.wallet(WalletCredits.class, wu -> wu.setAmount(5));

		for (int i = 0; i < 5; ++i) {
			c.draw(nowhere);
			r.draw(nowhere);
		}
		g.start();

		// renvoi le DTO
		return new GameDTOEndpoint(g, builder, mapper);
	}

	public void setBuilder(GameDTOBuilder builder) {
		this.builder = builder;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public void setKeyMode(KeyMode keyMode) {
		this.keyMode = keyMode;
	}
}
