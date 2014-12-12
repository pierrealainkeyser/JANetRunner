package org.keyser.anr.core;

/**
 * Permet d'instancier une carte
 * 
 * @author PAF
 *
 */
public interface AbstractCardFactory {

	public AbstractCard create(int id, MetaCard meta);
}
