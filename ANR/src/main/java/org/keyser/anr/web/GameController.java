package org.keyser.anr.web;

import org.keyser.anr.core.MetaGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Ca ne marche pas encore en java 8
 * 
 * @author PAF
 * 
 */
@Controller
public class GameController {

	private final GameRepository repository;

	@Autowired
	public GameController(GameRepository repository) {
		this.repository = repository;
	}
	
	@RequestMapping(value = "gsap")
	public String play(){
		return "gsap";
	}

	@RequestMapping(value = "play/{gameId}")
	public String play(@PathVariable String gameId, Model model) {
		model.addAttribute("gameId", gameId);

		GameAccess ga = repository.get(gameId);
		if (ga != null) {
			MetaGame meta = ga.getGateway().getMetaGame();
			model.addAttribute("corp", meta.getCorp().name().toLowerCase());
			model.addAttribute("runner", meta.getRunner().name().toLowerCase());
		}

		return "play";
	}
}
