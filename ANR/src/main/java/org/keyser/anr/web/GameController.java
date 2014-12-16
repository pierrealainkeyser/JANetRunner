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

	
	
	@RequestMapping(value = "gsap")
	public String play(){
		return "gsap";
	}

}
