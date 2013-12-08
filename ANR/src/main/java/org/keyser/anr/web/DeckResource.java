package org.keyser.anr.web;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.Resource;

public class DeckResource {

	private Resource resource;

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public InputStream openStream() throws IOException {
		return resource.getInputStream();
	}
}
