package org.keyser.anr.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.keyser.anr.core.corp.Corp;

public class TestOCTGNParser {

	@Test
	public void testParser() throws FileNotFoundException, IOException {
		OCTGNParser p = new OCTGNParser();

		try (InputStream fis = TestOCTGNParser.class.getResourceAsStream("/core-nbn.o8d")) {
			Corp c = p.parseCorp(fis);
			System.out.println(c.getClass().getName());

			Map<String, Class<?>> ids = new HashMap<>();
			c.getStack().stream().forEach(card -> {
				Class<? extends Card> cl = card.getClass();

				CardDef cd = cl.getAnnotation(CardDef.class);
				System.out.println(cl.getName() + " " + cd.oid());
				Class<?> old = ids.get(cd.oid());
				if (old == null)
					ids.put(cd.oid(), cl);
				else
					Assert.assertEquals("Les classes devrait être identiques", cl, old);

			});

			// il s'agit d'un deck de 49 cartes
			Assert.assertEquals(49, c.getStack().size());
		}
	}
}
