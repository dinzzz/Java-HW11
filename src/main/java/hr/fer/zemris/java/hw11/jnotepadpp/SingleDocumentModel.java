package hr.fer.zemris.java.hw11.jnotepadpp;

import java.nio.file.Path;

import javax.swing.JTextArea;

/**
 * Interface that describes the single document model. This model represents a
 * single text document opened in the Notepad++ editor.
 * 
 * @author Dinz
 *
 */
public interface SingleDocumentModel {

	/**
	 * Gets the text components of the model.
	 * 
	 * @return Text component of the model.
	 */
	JTextArea getTextComponent();

	/**
	 * Gets the location on the disk where the document is stored.
	 * 
	 * @return Location of the document.
	 */
	Path getFilePath();

	/**
	 * Sets the location of the document.
	 * 
	 * @param path
	 *            New location of the document.
	 */
	void setFilePath(Path path);

	/**
	 * Method that checks if the document has been modified.
	 * 
	 * @return True if the document has been modified, false otherwise.
	 */
	boolean isModified();

	/**
	 * Sets the modification flag of the document when the modification status
	 * changes.
	 * 
	 * @param modified
	 *            Modification flag.
	 */
	void setModified(boolean modified);

	/**
	 * Adds the listener to the single document model.
	 * 
	 * @param l
	 *            Listener to be added.
	 */
	void addSingleDocumentListener(SingleDocumentListener l);

	/**
	 * Removes the listener from the single document model.
	 * 
	 * @param l
	 *            Listener to be removed.
	 */
	void removeSingleDocumentListener(SingleDocumentListener l);
}
