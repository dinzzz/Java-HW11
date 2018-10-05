package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that represents a basic type of localization provider, used in
 * localization/internationalization of the programs.
 * 
 * @author Dinz
 *
 */
public abstract class AbstractLocalizationProvider implements ILocalizationProvider {

	/**
	 * List of localization listeners.
	 */
	List<ILocalizationListener> listeners;

	/**
	 * Constructs a new localization provider.
	 */
	public AbstractLocalizationProvider() {
		listeners = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addLocalizationListener(ILocalizationListener listener) {
		listeners.add(listener);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeLocalizationListener(ILocalizationListener listener) {
		listeners.remove(listener);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String getString(String string);

	/**
	 * Method that notifies the listeners about the localization change.
	 */
	public void fire() {
		for (ILocalizationListener listener : listeners) {
			listener.localizationChanged();
		}
	}

}
