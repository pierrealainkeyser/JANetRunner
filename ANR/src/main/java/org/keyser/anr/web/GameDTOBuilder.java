package org.keyser.anr.web;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CardLocation.Where;
import org.keyser.anr.core.CardLocationIce;
import org.keyser.anr.core.CardLocationOnServer;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Notification;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.Response;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.WalletActions;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.WalletUnit;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.corp.CorpArchivesServer;
import org.keyser.anr.core.corp.CorpCard;
import org.keyser.anr.core.corp.CorpHQServer;
import org.keyser.anr.core.corp.CorpRDServer;
import org.keyser.anr.core.corp.CorpRemoteServer;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.runner.Runner;
import org.keyser.anr.core.runner.RunnerCard;
import org.keyser.anr.web.dto.CardDTO;
import org.keyser.anr.web.dto.CardDefDTO;
import org.keyser.anr.web.dto.GameDTO;
import org.keyser.anr.web.dto.LocationDTO;
import org.keyser.anr.web.dto.PlayerDTO;
import org.keyser.anr.web.dto.QuestionDTO;
import org.keyser.anr.web.dto.QuestionDTO.PossibleResponseDTO;

/**
 * Permet de convertir les {@link Game} et {@link Notification} en
 * {@link GameDTO}.
 * 
 * @author PAF
 * 
 */
public class GameDTOBuilder {

	/**
	 * Prise en compte des notifications
	 * 
	 * @param ins
	 * @return
	 */
	public GameDTO notifs(List<Notification> ins) {
		GameDTO n = new GameDTO();
		ins.stream().forEach(i -> update(n, i));
		return n;
	}

	/**
	 * Mise à jour du jeu avec les DTO
	 * 
	 * @param g
	 * @param i
	 */
	private void update(GameDTO g, Notification i) {
		if (i instanceof Question)
			updateQuestion(g, (Question) i);
		else {

			NotificationEvent type = i.getType();
			if (NotificationEvent.WALLET_CHANGED == type)
				updateWallet(g, i);
			else if (NotificationEvent.CARD_LOC_CHANGED == type) {
				Card c = i.getCard();
				CardDTO dto = new CardDTO().setId(id(c)).setLocation(location(c));
				if (c instanceof CorpCard) {
					CorpCard cc = (CorpCard) c;
					dto.setVisible(cc.isRezzed());
				}
				g.addCard(dto);
			} else if (NotificationEvent.NEXT_STEP == type) {
				g.setStep(i.getStep());
			}
		}
	}

	/**
	 * Création de la réponse possible
	 * 
	 * @param r
	 * @return
	 */
	private PossibleResponseDTO possibleReponse(Response r) {
		Integer i = null;
		if (r.getCard() != null)
			i = r.getCard().getId();
		return new PossibleResponseDTO(r.getOption(), r.getResponseId(), i, r.getContent());
	}

	/**
	 * Mise à jour de la question
	 * 
	 * @param g
	 * @param q
	 */
	private void updateQuestion(GameDTO g, Question q) {

		QuestionDTO d = new QuestionDTO(q.getQid(), q.getTo(), q.getType());
		q.getResponses().values().forEach(r -> d.add(possibleReponse(r)));
		g.setQuestion(d);
	}

	private void updateWallet(GameDTO g, Notification i) {
		WalletUnit wu = i.getWalletUnit();
		PlayerDTO p = g.create(wu.getPlayer());
		int amount = wu.getAmount();

		// TODO gestion des autres types de wallet
		if (wu instanceof WalletCredits)
			p.setValue("credits", amount);
		else if (wu instanceof WalletActions)
			p.setValue("actions", amount);
	}

	/**
	 * Création de la copie complete
	 * 
	 * @param game
	 * @return
	 */
	public GameDTO createGameDTO(Game game) {
		GameDTO g = new GameDTO();
		g.setStep(game.getStep());

		Corp corp = game.getCorp();
		Runner runner = game.getRunner();

		// rajout le DTO de la corp
		g.setCorp(corpDTO(corp));
		g.addCard(new CardDTO().setDef(new CardDefDTO("corp", getURL(corp), "corp")).setLocation(LocationDTO.hq_id).setVisible(true));

		// gestion du runner
		g.setRunner(runnerDTO(runner));
		g.addCard(new CardDTO().setDef(new CardDefDTO("runner", getURL(runner), "runner")).setLocation(LocationDTO.grip_id).setVisible(true));

		Consumer<Card> add = c -> g.addCard(card(c));

		// rajout des cartes de tous le plateau
		corp.forEach(add);

		runner.getHand().stream().forEach(add);
		runner.getDiscard().stream().forEach(add);
		runner.getStack().stream().forEach(add);

		// mise à jours des questions
		game.getQuestions().values().stream().forEach(q -> updateQuestion(g, q));

		return g;
	}

	private PlayerDTO corpDTO(Corp corp) {
		PlayerDTO p = new PlayerDTO();

		Wallet w = corp.getWallet();
		p.setValue("credits", w.amountOf(WalletCredits.class));
		p.setValue("actions", w.amountOf(WalletActions.class));

		List<Integer> r = new ArrayList<>();
		corp.listRemotes().forEach(c -> r.add(c.getId() + 3));
		p.setServers(r);

		return p;
	}

	private PlayerDTO runnerDTO(Runner runner) {
		PlayerDTO p = new PlayerDTO();

		Wallet w = runner.getWallet();
		p.setValue("credits", w.amountOf(WalletCredits.class));
		p.setValue("actions", w.amountOf(WalletActions.class));

		return p;
	}

	private CardDTO card(Card c) {
		CardDTO dto = new CardDTO().setDef(def(c)).setLocation(location(c));
		if (c instanceof CorpCard) {
			CorpCard cc = (CorpCard) c;
			dto.setVisible(cc.isRezzed());
		}
		return dto;
	}

	private LocationDTO location(Card c) {
		CardLocation cl = c.getLocation();
		if (cl == CardLocation.ARCHIVES)
			return LocationDTO.archives;
		else if (cl == CardLocation.HQ)
			return LocationDTO.hq;
		else if (cl == CardLocation.RD)
			return LocationDTO.rd;
		if (cl == CardLocation.HEAP)
			return LocationDTO.heap;
		else if (cl == CardLocation.GRIP)
			return LocationDTO.grip;
		else if (cl == CardLocation.STACK)
			return LocationDTO.stack;
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

	private LocationDTO serverLocation(CardLocationOnServer i) {
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

	private String id(Card c) {
		return "" + c.getId();
	}

	private CardDefDTO def(Card c) {
		String faction = "corp";
		if (c instanceof RunnerCard)
			faction = "runner";

		// permet de gerer le format des cards en prenant une URL avec une oid
		String url = getURL(c);

		return new CardDefDTO(id(c), url, faction);
	}

	private String getURL(Object c) {
		CardDef cd = c.getClass().getAnnotation(CardDef.class);
		return cd.oid();
	}
}
