package org.keyser.anr.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * 
 * 
 * @author PAF
 * 
 */
@Controller
public class GameController {

	@Autowired
	private ResourceUrlProvider provider;

	private final static Pattern mainJsPattern = Pattern.compile("^/js/(main-.*)\\.js$");

	@RequestMapping(value = "gsap")
	public ModelAndView play(@RequestParam(defaultValue = "456") String id) {

		ModelAndView mav = new ModelAndView("gsap_require");
		mav.addObject("gameId", id);

		String main = provider.getForLookupPath("/js/main.js");
		Matcher matcher = mainJsPattern.matcher(main);
		if (matcher.matches()) {
			String mainVersion = matcher.group(1);
			mav.addObject("mainJs", mainVersion);
		} else
			mav.addObject("mainJs", "main");

		return mav;
	}

}
