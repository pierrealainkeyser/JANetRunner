package org.keyser.anr.core;

import java.util.HashMap;
import java.util.Map;

public final class MetaCards {

	private Map<String, MetaCard> metas = new HashMap<>();

	public void add(MetaCard meta) {
		metas.put(meta.getName(), meta);
	}

	public MetaCard get(String name) {
		return metas.get(name);
	}
}
