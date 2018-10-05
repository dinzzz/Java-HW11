package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * Class that represents a localization provider bridge which connects
 * localization providers with appropriate localization listeners.
 * 
 * @author Dinz
 *
 */
public class LocalizationProviderBridge extends AbstractLocalizationProvider {

	/**
	 * Flag that notes if the localization bridge is currently connected with any
	 * listeners.
	 */
	boolean connected;

	/**
	 * Localization provider.
	 */
	ILocalizationProvider provider;

	/**
	 * Localization listener.
	 */
	ILocalizationListener listener;

	/**
	 * Constructs a new localization provider bridge.
	 * 
	 * @param provider
	 *            Localization provider.
	 */
	public LocalizationProviderBridge(ILocalizationProvider provider) {
		this.provider = provider;
		this.listener = new ILocalizationListener() {
			@Override
			public void localizationChanged() {
				fire();

			}

		};
	}

	/**
	 * Connects a localization listener to the localization provider.
	 */
	public void connect() {
		if (connected == true) {
			return;
		}

		provider.addLocalizationListener(listener);
		connected = true;

	}

	/**
	 * Disconnects a localization listener from the localization provider.
	 */
	public void disconnect() {
		provider.removeLocalizationListener(listener);
		connected = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(String string) {
		return provider.getString(string);
	}

}
