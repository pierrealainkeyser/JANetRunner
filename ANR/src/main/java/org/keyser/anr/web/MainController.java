package org.keyser.anr.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Ca ne marche pas encore en java 8
 * @author PAF
 *
 */
@Controller
public class MainController {

	@RequestMapping(value = "/")
	@ResponseBody
	public Resource index() {
		return new ClassPathResource("index.html");
	}
}
