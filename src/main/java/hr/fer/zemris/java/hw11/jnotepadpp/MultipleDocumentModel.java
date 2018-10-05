package hr.fer.zemris.java.hw11.jnotepadpp;

import java.nio.file.Path;

/**
 * Interface which describes multiple document model.
 * 
 * @author Dinz
 *
 */
public interface MultipleDocumentModel extends Iterable<SingleDocumentModel> {

	/**
	 * Method that creates a new document in the editor.
	 * 
	 * @return newly created document.
	 */
	SingleDocumentModel createNewDocument();

	/**
	 * Gets the current document.
	 * 
	 * @return Currently opened document.
	 */
	SingleDocumentModel getCurrentDocument();

	/**
	 * Method that loads the document from the disk.
	 * 
	 * @param path
	 *            Location of the document on the disk.
	 * @return Newly loaded document.
	 */
	SingleDocumentModel loadDocument(Path path);

	/**
	 * Method that saves the document to the desired location on the disk.
	 * 
	 * @param model
	 *            Document to be saved.
	 * @param newPath
	 *            Location on the disk where the document should be saved.
	 */
	void saveDocument(SingleDocumentModel model, Path newPath);

	/**
	 * Method that closes the currently opened document.
	 * 
	 * @param model
	 *            Document to be closed.
	 */
	void closeDocument(SingleDocumentModel model);

	/**
	 * Adds a new listener to the multiple document model-
	 * 
	 * @param l
	 *            Listener to be added.
	 */
	void addMultipleDocumentListener(MultipleDocumentListener l);

	/**
	 * Removes the listener from the multiple document model.
	 * 
	 * @param l
	 *            Listener to be removed.
	 */
	void removeMultipleDocumentListener(MultipleDocumentListener l);

	/**
	 * Gets the number of documents in the multiple document model.
	 * 
	 * @return Number of documents.
	 */
	int getNumberOfDocuments();

	/**
	 * Gets the document at the given index.
	 * 
	 * @param index
	 *            Index.
	 * @return Single document model.
	 */
	SingleDocumentModel getDocument(int index);
}
