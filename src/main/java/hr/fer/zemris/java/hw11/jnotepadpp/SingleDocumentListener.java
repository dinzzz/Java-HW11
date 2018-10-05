package hr.fer.zemris.java.hw11.jnotepadpp;

/**
 * Interface that represents a single document listener. Listener which notifies
 * the multiple document model when the single document is modified or it's
 * location on the disk changes.
 * 
 * @author Dinz
 *
 */
public interface SingleDocumentListener {

	/**
	 * Method that notifies the model when the single document model has been
	 * modified.
	 * 
	 * @param model
	 *            Currently opened document model.
	 */
	void documentModifyStatusUpdated(SingleDocumentModel model);

	/**
	 * Method that notifies the model when the single document model has its
	 * location on the disk changed.
	 * 
	 * @param model
	 *            Currently opened document model.
	 */
	void documentFilePathUpdated(SingleDocumentModel model);
}
