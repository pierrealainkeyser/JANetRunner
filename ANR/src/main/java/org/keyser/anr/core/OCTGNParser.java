package org.keyser.anr.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.keyser.anr.core.corp.AllCorp;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.corp.CorpCard;
import org.keyser.anr.core.runner.AllRunner;
import org.keyser.anr.core.runner.Runner;
import org.keyser.anr.core.runner.RunnerCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Permet de parser les fichiers .o8d pour une corp
 * 
 * @author PAF
 * 
 */
public class OCTGNParser {

	private AllCorp corps = new AllCorp();

	private AllRunner runners = new AllRunner();

	private final static Logger logger = LoggerFactory.getLogger(OCTGNParser.class);

	private <P, C> P parse(InputStream is, Function<String, P> identity, Function<String, C> cards, BiConsumer<P, C> consummer) {

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
							C c = cards.apply(text);
							if (c != null)
								consummer.accept(p, c);
							else {
								logger.warn("Card not found : {}", text);
							}
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
	public Corp parseCorp(InputStream is) {
		return parse(is, corps::newCorp, (Function<String, CorpCard>) corps::newCard, Corp::addToRD);
	}

	/**
	 * Permet de parser un runner
	 * 
	 * @param is
	 * @return
	 */
	public Runner parseRunner(InputStream is) {
		return parse(is, runners::newRunner, (Function<String, RunnerCard>) runners::newCard, Runner::addToStack);
	}

}
