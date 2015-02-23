package org.keyser.anr.core;

import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.keyser.anr.core.corp.CorpServerDef;
import org.keyser.anr.core.corp.nbn.MakingNews;
import org.keyser.anr.core.runner.shaper.KateMcCaffrey;

public class TestOCTGNParser {

	@Test
	public void testCorpParser() throws Exception {
		OCTGNParser p = new OCTGNParser();
		try (InputStream fis = TestOCTGNParser.class.getResourceAsStream("/core-nbn.o8d")) {
			CorpDef c = p.parseCorp(fis);

			List<CorpServerDef> servers = c.getServers();
			Assert.assertNotNull(servers);
			Assert.assertEquals(1, servers.size());
			Assert.assertEquals(49, servers.get(0).getStack().size());
			Assert.assertEquals(MakingNews.INSTANCE.getName(), c.getName());
		}
	}

	@Test
	public void testRunnerParser() throws Exception {
		OCTGNParser p = new OCTGNParser();
		try (InputStream fis = TestOCTGNParser.class.getResourceAsStream("/core-shapper.o8d")) {
			RunnerDef c = p.parseRunner(fis);

			List<AbstractCardDef> stack = c.getStack();
			Assert.assertNotNull(stack);
			Assert.assertEquals(47, stack.size());
			Assert.assertEquals(KateMcCaffrey.INSTANCE.getName(), c.getName());
		}
	}

	@Test
	public void testCreateCoreGame() throws Exception {

		GameDef def = new GameDef();
		OCTGNParser p = new OCTGNParser();
		try (InputStream fis = TestOCTGNParser.class.getResourceAsStream("/core-shapper.o8d")) {
			def.setRunner(p.parseRunner(fis));
		}

		try (InputStream fis = TestOCTGNParser.class.getResourceAsStream("/core-nbn.o8d")) {
			def.setCorp(p.parseCorp(fis));
		}

		Game g = new Game();
		g.load(def, ANRMetaCards.INSTANCE);
		Corp corp = g.getCorp();
		Runner runner = g.getRunner();
		
		Assert.assertNotNull(corp);
		Assert.assertNotNull(runner);

		Assert.assertEquals(49, corp.getRd().getStack().getContents().size());
		Assert.assertEquals(47, runner.getStack().getContents().size());
	}
}
