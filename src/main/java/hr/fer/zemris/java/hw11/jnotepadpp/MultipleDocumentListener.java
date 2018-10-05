package hr.fer.zemris.java.hw11.jnotepadpp;

/**
 * Interface that represents a multiple document listener. This listeners is
 * being notified about current document being changed and about the document
 * being added to or removed from the editor.
 * 
 * @author Dinz
 *
 */
public interface MultipleDocumentListener {
	/**
	 * Method that executes when the current document has been changed.
	 * 
	 * @param previousModel
	 *            Previous document.
	 * @param currentModel
	 *            Currently opened document.
	 */
	void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel);

	/**
	 * Method that executes when the document has been added to multiple document
	 * model.
	 * 
	 * @param model
	 *            Newly added document.
	 */
	void documentAdded(SingleDocumentModel model);

	/**
	 * Method that executes when the document has been removed from the multiple
	 * document model.
	 * 
	 * @param model
	 *            Document that has been removed.
	 */
	void documentRemoved(SingleDocumentModel model);
}
