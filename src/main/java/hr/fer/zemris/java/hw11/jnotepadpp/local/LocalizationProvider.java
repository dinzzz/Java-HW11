package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class that represents a localization provider, which provides localization
 * for the programs. That means that these programs support
 * internationalization, support for working on multiple languages. It uses a singleton 
 * pattern, which means only one instance of the class is allowed.
 *
 * @author Dinz
 *
 */
public class LocalizationProvider extends AbstractLocalizationProvider {
	
	/**
	 * Currently set language.
	 */
	private String language;
	
	/**
	 * Resource bundle used which stores literal text represented to the user.
	 */
	private ResourceBundle bundle;
	
	/**
	 * Localization provider instance which supports the singleton pattern.
	 */
	private static LocalizationProvider instance = new LocalizationProvider();
	
	/**
	 * Constructs a new localization provider with language set to English.
	 */
	private LocalizationProvider() {
		this.setLanguage("en");
	}
	
	/**
	 * Gets the instance of the localization provider.
	 * @return Instance of the localization provider.
	 */
	public static LocalizationProvider getInstance() {
		return instance;
	}
	
	/**
	 * Sets the language of the localization provider.
	 * @param language Language to be set.
	 */
	public void setLanguage(String language) {
		this.language = language;
		bundle = ResourceBundle.getBundle("hr.fer.zemris.java.hw11.jnotepadpp.local.translate",
				Locale.forLanguageTag(language));
		this.fire();
	}
	
	/**
	 * Gets currently set language.
	 * @return Currently set language.
	 */
	public String getLanguage() {
		return this.language;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(String string) {
		return bundle.getString(string);
	}

}
