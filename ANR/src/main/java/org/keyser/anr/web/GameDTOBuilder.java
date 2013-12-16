package org.keyser.anr.web;

import java.util.List;
import java.util.function.BiConsumer;
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
import org.keyser.anr.core.PlayableUnit;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.Response;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.WalletActions;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.WalletRecuringCredits;
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

	private static final String AGENDA = "agenda";
	private static final String RECURING = "recuring";
	private static final String SCORE = "score";
	private static final String RUNNER = "runner";
	private static final String CORP = "corp";
	private static final String ACTIONS = "actions";
	private static final String POWER = "power";
	private static final String CREDITS = "credits";

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
			if (NotificationEvent.WALLET_CHANGED == type) {
				WalletUnit wu = i.getWalletUnit();
				updateWallet(g, g.create(wu.getPlayer()), wu);
			} else if (NotificationEvent.CARD_LOC_CHANGED == type) {
				handleCardNotif(g, i, (c, dto) -> {
					dto.setLocation(location(c));

					if (c instanceof CorpCard) {
						CorpCard cc = (CorpCard) c;
						dto.setVisible(cc.isRezzed());
					}
				});
			} else if (NotificationEvent.CARD_REZZ_CHANGED == type) {
				handleCardNotif(g, i, (c, dto) -> {
					if (c instanceof CorpCard) {
						CorpCard cc = (CorpCard) c;
						dto.setVisible(cc.isRezzed());
					}
				});
			} else if (NotificationEvent.CARD_POWER_COUNTER == type || NotificationEvent.CARD_CREDITS == type) {
				handleCardNotif(g, i, this::updateTokens);
			} else if (NotificationEvent.CARD_ADVANCED == type) {
				handleCardNotif(g, i, (c, dto) -> {
					if (c instanceof CorpCard) {
						CorpCard cc = (CorpCard) c;
						dto.addToken(AGENDA, cc.getAdvancement());
					}
				});
			} else if (NotificationEvent.NEXT_STEP == type) {
				g.setStep(i.getStep());
			} else if (NotificationEvent.CORP_SCORE_AGENDA == type) {
				// TODO

			} else if (NotificationEvent.RUNNER_SCORE_AGENDA == type) {
				// TODO
			}
		}
	}

	private void handleCardNotif(GameDTO g, Notification i, BiConsumer<Card, CardDTO> consumer) {
		Card c = i.getCard();
		CardDTO dto = g.getCard(id(c));
		consumer.accept(c, dto);
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

	private void updateScore(PlayableUnit pu, GameDTO g, PlayerDTO p) {
		p.setValue(SCORE, pu.getScore());
	}

	private void updateWallet(GameDTO g, PlayerDTO p, WalletUnit wu) {
		int amount = wu.getAmount();

		// TODO gestion des autres types de wallet
		if (wu instanceof WalletCredits)
			p.setValue(CREDITS, amount);
		else if (wu instanceof WalletActions)
			p.setValue(ACTIONS, amount);
		else if (wu instanceof WalletRecuringCredits) {
			WalletRecuringCredits wrc = (WalletRecuringCredits) wu;
			Card c = wrc.getCard();
			CardDTO dto = g.getCard(c != null ? id(c) : wrc.getFaction());
			dto.addToken(RECURING, wrc.getAmount());
		}

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
		g.addCard(new CardDTO().setDef(new CardDefDTO(CORP, getURL(corp), CORP)).setLocation(LocationDTO.hq_id).setVisible(true));
		g.setCorp(corpDTO(g, corp));

		// gestion du runner
		g.addCard(new CardDTO().setDef(new CardDefDTO(RUNNER, getURL(runner), RUNNER)).setLocation(LocationDTO.grip_id).setVisible(true));
		g.setRunner(runnerDTO(g, runner));

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

	private PlayerDTO corpDTO(GameDTO g, Corp corp) {
		PlayerDTO p = new PlayerDTO();

		Wallet w = corp.getWallet();
		updateScore(corp, g, p);
		w.forEach(wu -> updateWallet(g, p, wu));

		return p;
	}

	private PlayerDTO runnerDTO(GameDTO g, Runner runner) {
		PlayerDTO p = new PlayerDTO();

		Wallet w = runner.getWallet();
		updateScore(runner, g, p);
		w.forEach(wu -> updateWallet(g, p, wu));

		return p;
	}

	private CardDTO updateTokens(Card c, CardDTO dto) {
		Integer credits = c.getCredits();
		if (credits != null)
			dto.addToken(CREDITS, credits);

		Integer powerCounter = c.getPowerCounter();
		if (powerCounter != null)
			dto.addToken(POWER, powerCounter);
		return dto;

	}

	private CardDTO card(Card c) {
		CardDTO dto = new CardDTO().setDef(def(c)).setLocation(location(c));
		updateTokens(c, dto);
		if (c instanceof CorpCard) {
			CorpCard cc = (CorpCard) c;
			dto.setVisible(cc.isRezzed());

			Integer adv = cc.getAdvancement();
			if (adv != null)
				dto.addToken(AGENDA, adv);
		}
		return dto;
	}

	private LocationDTO location(Card c) {

		// FIXME gestion des zones de score

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
			} else if (w == Where.ASSET) {
				return serverLocation((CardLocationOnServer) cl);
			} else if (w == Where.UPGRADE) {
				return LocationDTO.upgrade(serverLocation((CardLocationOnServer) cl));
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
		String faction = CORP;
		if (c instanceof RunnerCard)
			faction = RUNNER;

		// permet de gerer le format des cards en prenant une URL avec une oid
		String url = getURL(c);

		return new CardDefDTO(id(c), url, faction);
	}

	private String getURL(Object c) {
		CardDef cd = c.getClass().getAnnotation(CardDef.class);
		return cd.oid();
	}
}
