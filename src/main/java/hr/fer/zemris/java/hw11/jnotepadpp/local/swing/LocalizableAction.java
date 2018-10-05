package hr.fer.zemris.java.hw11.jnotepadpp.local.swing;

import javax.swing.AbstractAction;

import hr.fer.zemris.java.hw11.jnotepadpp.local.ILocalizationListener;
import hr.fer.zemris.java.hw11.jnotepadpp.local.ILocalizationProvider;

/**
 * Abstract class that represents a basic localizable action that has changed
 * text representation when the language of the program changes.
 * 
 * @author Dinz
 *
 */
public abstract class LocalizableAction extends AbstractAction {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = -1777201051531671371L;

	/**
	 * Key of the text value to be presented.
	 */
	String key;

	/**
	 * Constructs a new localizable action.
	 * 
	 * @param key
	 *            Key of the text value to be presented.
	 * @param provider
	 *            Localization provider.
	 */
	public LocalizableAction(String key, ILocalizationProvider provider) {
		String translation = provider.getString(key);
		this.putValue(NAME, translation);

		provider.addLocalizationListener(new ILocalizationListener() {

			@Override
			public void localizationChanged() {
				String translation = provider.getString(key);
				LocalizableAction.this.putValue(NAME, translation);

			}
		});

	}

}
