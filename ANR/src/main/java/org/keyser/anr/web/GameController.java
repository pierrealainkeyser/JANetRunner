package org.keyser.anr.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Ca ne marche pas encore en java 8
 * 
 * @author PAF
 * 
 */
@Controller
public class GameController {

	@RequestMapping(value = "gsap")
	public ModelAndView play(@RequestParam(defaultValue="456") String id) {

		ModelAndView mav = new ModelAndView("gsap_require");
		mav.addObject("gameId", id);
		return mav;
	}

}
