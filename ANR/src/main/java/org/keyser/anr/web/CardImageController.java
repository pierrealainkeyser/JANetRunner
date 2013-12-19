package org.keyser.anr.web;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

@Controller
public class CardImageController {

	private final RemoteCardLoader cardLoarder;

	@Autowired
	public CardImageController(RemoteCardLoader cardLoarder) {
		this.cardLoarder = cardLoarder;
	}

	@RequestMapping(value = "/card-img/{url}")
	@ResponseBody
	public DeferredResult<ResponseEntity<Resource>> lookupImage(HttpServletResponse response, @PathVariable("url") String url,
			@RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedSince) {
		DeferredResult<ResponseEntity<Resource>> dr = new DeferredResult<>();

		HttpHeaders req = null;
		if (ifModifiedSince != null) {
			req = new HttpHeaders();
			req.set("If-Modified-Since", ifModifiedSince);
		}

		cardLoarder.load(dr, url, req);
		return dr;
	}
}
