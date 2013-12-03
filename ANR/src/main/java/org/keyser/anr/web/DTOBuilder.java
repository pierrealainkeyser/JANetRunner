package org.keyser.anr.web;

import java.text.MessageFormat;
import java.util.function.Consumer;

import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CardLocation.Where;
import org.keyser.anr.core.CardLocationIce;
import org.keyser.anr.core.CardLocationOnServer;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.corp.CorpArchivesServer;
import org.keyser.anr.core.corp.CorpHQServer;
import org.keyser.anr.core.corp.CorpRDServer;
import org.keyser.anr.core.corp.CorpRemoteServer;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.runner.RunnerCard;
import org.keyser.anr.web.dto.CardDTO;
import org.keyser.anr.web.dto.CardDefDTO;
import org.keyser.anr.web.dto.GameDTO;
import org.keyser.anr.web.dto.LocationDTO;

public class DTOBuilder {

	private String urlFormat;

	public void setUrlFormat(String urlFormat) {
		this.urlFormat = urlFormat;
	}

	public GameDTO createGameDTO(Game game) {
		GameDTO g = new GameDTO();
		Corp corp = game.getCorp();

		Consumer<Card> add = c -> g.addCard(card(c));

		corp.getHand().stream().forEach(add);
		corp.getDiscard().stream().forEach(add);
		corp.getStack().stream().forEach(add);
		
		
		//rajout le DTO de la corp
		g.addCard(new CardDTO().setDef(new CardDefDTO("corp",  getURL(corp), "corp")).setLocation(LocationDTO.hq_id).setVisible(true));
		
		

		return g;
	}

	public CardDTO card(Card c) {
		return new CardDTO().setDef(def(c)).setLocation(location(c));
	}

	public LocationDTO location(Card c) {
		CardLocation cl = c.getLocation();
		if (cl == CardLocation.ARCHIVES)
			return LocationDTO.archives;
		else if (cl == CardLocation.HQ)
			return LocationDTO.hq;
		else if (cl == CardLocation.RD)
			return LocationDTO.rd;
		else {
			CardLocation.Where w = cl.getWhere();
			if (w == Where.ICE) {
				CardLocationIce i = (CardLocationIce) cl;
				return LocationDTO.ice(serverLocation(i), i.getIndex());
			} else if (w == Where.UPGRADE) {
				// TODO
			}
		}

		return null;
	}

	public LocationDTO serverLocation(CardLocationOnServer i) {
		LocationDTO s = null;
		CorpServer ser = i.getServer();
		if (ser instanceof CorpRDServer)
			s = LocationDTO.rd;
		else if (ser instanceof CorpArchivesServer)
			s = LocationDTO.archives;
		else if (ser instanceof CorpHQServer)
			s = LocationDTO.hq;
		else if (ser instanceof CorpRemoteServer) {
			CorpRemoteServer r = (CorpRemoteServer) ser;
			s = LocationDTO.remote(r.getId());
		}
		return s;
	}

	public CardDefDTO def(Card c) {
		String faction = "corp";
		if (c instanceof RunnerCard)
			faction = "runner";

		// permet de gerer le format des cards en prenant une URL avec une oid
		String url = getURL(c);

		return new CardDefDTO("" + c.getId(), url, faction);
	}

	private String getURL(Object c) {
		CardDef cd = c.getClass().getAnnotation(CardDef.class);
		String url = MessageFormat.format(urlFormat, cd.oid());
		return url;
	}
}
