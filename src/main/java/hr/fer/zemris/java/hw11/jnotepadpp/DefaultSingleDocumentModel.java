package hr.fer.zemris.java.hw11.jnotepadpp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Class that represents a single document model. This document model is
 * representing an actual text file which is being edited or created in
 * Notepad++ program. This document also notifies the multiple document model
 * about it being changed when the user does some editing.
 * 
 * @author Dinz
 *
 */
public class DefaultSingleDocumentModel implements SingleDocumentModel {

	/**
	 * Path of the document on the disk.
	 */
	private Path filePath;

	/**
	 * Text component of the document.
	 */
	private JTextArea textComponent;

	/**
	 * List of listeners of the single document model.
	 */
	List<SingleDocumentListener> listeners = new ArrayList<>();

	/**
	 * Flag that denotes if the document has been modified.
	 */
	boolean modified = false;

	/**
	 * Constructs a new default single document model.
	 * 
	 * @param filePath
	 *            Path of the new document.
	 * @param textContent
	 *            Text content of the document.
	 */
	public DefaultSingleDocumentModel(Path filePath, String textContent) {
		this.filePath = filePath;
		this.textComponent = new JTextArea(textContent);

		textComponent.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				DefaultSingleDocumentModel.this.setModified(true);

			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				DefaultSingleDocumentModel.this.setModified(true);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				DefaultSingleDocumentModel.this.setModified(true);

			}

		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JTextArea getTextComponent() {
		return this.textComponent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path getFilePath() {
		return this.filePath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFilePath(Path path) {
		Objects.requireNonNull(path);
		this.filePath = path;
		notifyPathChangeListeners();
	}

	/**
	 * {@inheritDoc}
	 */
	private void notifyPathChangeListeners() {
		for (SingleDocumentListener listener : this.listeners) {
			listener.documentFilePathUpdated(this);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isModified() {
		return this.modified;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModified(boolean modified) {
		this.modified = modified;
		notifyListeners();

	}

	/**
	 * Method that notifies listeners about change happening in the document.
	 */
	private void notifyListeners() {
		for (SingleDocumentListener listener : this.listeners) {
			listener.documentModifyStatusUpdated(this);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSingleDocumentListener(SingleDocumentListener l) {
		listeners.add(l);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeSingleDocumentListener(SingleDocumentListener l) {
		listeners.remove(l);

	}

}
