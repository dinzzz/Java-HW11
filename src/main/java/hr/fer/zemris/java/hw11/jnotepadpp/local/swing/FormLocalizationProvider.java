package hr.fer.zemris.java.hw11.jnotepadpp.local.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import hr.fer.zemris.java.hw11.jnotepadpp.local.ILocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationProviderBridge;

/**
 * Class that represents a form localization provider, derived from the
 * localization provider bridge, specified for the action when the localization
 * is used in windows using the JFrame class.
 * 
 * @author Dinz
 *
 */
public class FormLocalizationProvider extends LocalizationProviderBridge {

	/**
	 * Constructs a new form localization provider.
	 * 
	 * @param provider
	 *            Localization provider.
	 * @param frame
	 *            Window where the localization/internationalization occurs.
	 */
	public FormLocalizationProvider(ILocalizationProvider provider, JFrame frame) {
		super(provider);
		frame.addWindowListener(new WindowAdapter() {

			public void windowOpened(WindowEvent e) {
				FormLocalizationProvider.this.connect();

			}

			public void windowClosed(WindowEvent e) {
				FormLocalizationProvider.this.disconnect();

			}

		});

	}

}
