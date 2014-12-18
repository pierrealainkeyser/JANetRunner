package org.keyser.anr.core;

/**
 * Permet de convertir des données recus dans un format quelconque vers un
 * format spécifique au jeu.
 * 
 * @author pakeyser
 *
 */
public interface UserInputConverter {

	public <T> T convert(Class<T> type, Game game, Object input);
}
