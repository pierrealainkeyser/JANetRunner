package org.keyser.anr.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.keyser.anr.core.corp.Corp;

public class TestOCTGNParser {

	@Test
	public void testParser() throws FileNotFoundException, IOException {
		OCTGNParser p = new OCTGNParser();

		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/core-nbn.o8d"))) {
			Corp c = p.parseCorp(fis);
			System.out.println(c.getClass().getName());
			c.getStack().stream().forEach(card -> System.out.println(card.getClass().getName()));
		}
	}
}
