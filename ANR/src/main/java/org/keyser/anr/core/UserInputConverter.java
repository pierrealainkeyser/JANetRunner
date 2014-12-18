package org.keyser.anr.core;

/**
 * Permet de convertir des donn�es recus dans un format quelconque vers un
 * format sp�cifique au jeu.
 * 
 * @author pakeyser
 *
 */
public interface UserInputConverter {

	public <T> T convert(Class<T> type, Game game, Object input);
}
