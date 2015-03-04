package org.keyser.anr.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.keyser.anr.core.corp.CorpServerDef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Permet de parser les fichiers .o8d pour une corp et un runner
 * 
 * @author PAF
 * 
 */
public class OCTGNParser {

	private <P> P parse(InputStream is, Function<String, P> identity, BiConsumer<P, AbstractCardDef> consummer) {

		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document d = db.parse(is);

			P p = null;
			NodeList sections = d.getElementsByTagName("section");
			for (int i = 0; i < sections.getLength(); ++i) {
				Element e = (Element) sections.item(i);
				String n = e.getAttribute("name");

				// gestion de l'identité
				if ("Identity".equals(n)) {
					NodeList childs = e.getElementsByTagName("card");
					Element card = (Element) childs.item(0);
					p = identity.apply(card.getTextContent());
				} else if ("R&D / Stack".equals(n)) {
					NodeList childs = e.getElementsByTagName("card");
					for (int j = 0; j < childs.getLength(); ++j) {
						Element card = (Element) childs.item(j);
						int qty = Integer.parseInt(card.getAttribute("qty"));
						String text = card.getTextContent();

						for (int z = 0; z < qty; ++z) {
							AbstractCardDef c = createCardDef(text);
							consummer.accept(p, c);
						}
					}
				}
			}
			return p;

		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new RuntimeException(e);

		}
	}

	/**
	 * Permet de parser une corporation
	 * 
	 * @param is
	 * @return
	 */
	public CorpDef parseCorp(InputStream is) {
		return parse(is, this::createCorpDef, this::addToRDStack);
	}

	/**
	 * Permet de parser un Runner
	 * 
	 * @param is
	 * @return
	 */
	public RunnerDef parseRunner(InputStream is) {
		return parse(is, this::createRunnerDef, this::addToRunnerStack);
	}

	private AbstractCardDef createCardDef(String name) {
		AbstractCardDef def = new AbstractCardDef();
		def.setName(name);
		return def;
	}

	private void addToRDStack(CorpDef corpDef, AbstractCardDef card) {
		corpDef.getServers().get(0).getStack().add(card);
	}

	private CorpDef createCorpDef(String name) {
		CorpDef def = new CorpDef();
		CorpServerDef rd = new CorpServerDef();
		rd.setId(-2);
		rd.setStack(new ArrayList<>());
		def.setServers(Arrays.asList(rd));
		def.setName(name);
		return def;
	}

	private void addToRunnerStack(RunnerDef runnerDef, AbstractCardDef card) {
		runnerDef.getStack().add(card);
	}

	private RunnerDef createRunnerDef(String name) {
		RunnerDef def = new RunnerDef();
		def.setStack(new ArrayList<>());
		def.setName(name);
		return def;
	}

}
