package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * Interface that describes the localization provider.
 * 
 * @author Dinz
 *
 */
public interface ILocalizationProvider {

	/**
	 * Adds the localization listener to the localization provider.
	 * 
	 * @param listener
	 *            Listener to be added.
	 */
	void addLocalizationListener(ILocalizationListener listener);

	/**
	 * Removes the listener from the localization provider.
	 * 
	 * @param listener
	 *            Listener to be removed.
	 */
	void removeLocalizationListener(ILocalizationListener listener);

	/**
	 * Method that gets the actual text shown in the program from the given key.
	 * 
	 * @param string
	 *            Key where the value is stored.
	 * @return Text to be shown in the program.
	 */
	String getString(String string);
}
