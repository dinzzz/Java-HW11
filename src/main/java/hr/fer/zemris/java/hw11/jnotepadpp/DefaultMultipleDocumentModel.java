package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

/**
 * This class directly represents a multiple document model which is used in
 * Notepad++. This is actually representing a tabbed pane that swing already
 * implements. The purpose of this class is to store single document models in
 * its tabs and receives/gives notifications of changes from the single
 * document's components.
 * 
 * @author Dinz
 *
 */
public class DefaultMultipleDocumentModel extends JTabbedPane implements MultipleDocumentModel {

	/**
	 * List of documents currently stored in multiple document model.
	 */
	List<SingleDocumentModel> documents = new ArrayList<>();

	/**
	 * List of listeners currently present in multiple document model.
	 */
	List<MultipleDocumentListener> listeners = new ArrayList<>();

	/**
	 * Icon that represents a file that is not saved after modification.
	 */
	ImageIcon notSaved;

	/**
	 * Icon that represents a saved, unmodified file.
	 */
	ImageIcon saved;

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = -2515137507092721994L;

	/**
	 * Constructs a new multiple document model.
	 */
	public DefaultMultipleDocumentModel() {
		try {
			notSaved = loadImage("icons/not-saved.png");
			saved = loadImage("icons/saved.png");

			this.addChangeListener(e -> {
				notifyChangeListeners();
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method that notifies listeners about change happening in one of the
	 * documents.
	 */
	void notifyChangeListeners() {
		for (MultipleDocumentListener listener : listeners) {
			if (this.getNumberOfDocuments() < 1) {
				listener.currentDocumentChanged(null, null);
				return;
			}
			listener.currentDocumentChanged(null, this.getCurrentDocument());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<SingleDocumentModel> iterator() {
		return documents.iterator();
	}

	private ImageIcon loadImage(String path) throws IOException {
		InputStream is = this.getClass().getResourceAsStream(path);

		if (is == null) {
			throw new IOException("Can't load an image.");
		}
		byte[] bytes = is.readAllBytes();
		is.close();
		return new ImageIcon(bytes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleDocumentModel createNewDocument() {
		SingleDocumentModel model = new DefaultSingleDocumentModel(null, "");
		notifyAddListeners(model);
		documents.add(model);
		this.add(model.getTextComponent());
		this.setSelectedIndex(this.getTabCount() - 1);
		model.setModified(true);
		this.setIconAt(this.getSelectedIndex(), saved);
		this.setTitleAt(this.getSelectedIndex(), "Untitled");

		model.addSingleDocumentListener(new SingleDocumentListener() {

			@Override
			public void documentModifyStatusUpdated(SingleDocumentModel model) {
				if (model.isModified()) {
					DefaultMultipleDocumentModel.this.setIconAt(DefaultMultipleDocumentModel.this.getSelectedIndex(),
							notSaved);

					notifyChangeListeners();
				} else {
					DefaultMultipleDocumentModel.this.setIconAt(DefaultMultipleDocumentModel.this.getSelectedIndex(),
							saved);
				}

			}

			@Override
			public void documentFilePathUpdated(SingleDocumentModel model) {
				DefaultMultipleDocumentModel.this.setTitleAt(DefaultMultipleDocumentModel.this.getSelectedIndex(),
						model.getFilePath().getFileName().toString());

				DefaultMultipleDocumentModel.this.setToolTipTextAt(DefaultMultipleDocumentModel.this.getSelectedIndex(),
						model.getFilePath().toString());

			}

		});

		model.getTextComponent().addCaretListener(e -> {
			notifyChangeListeners();
		});

		model.getTextComponent().addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				notifyChangeListeners();

			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}

		});

		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleDocumentModel getCurrentDocument() {
		return this.getDocument(this.getSelectedIndex());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleDocumentModel loadDocument(Path path) {
		Objects.requireNonNull(path);

		Iterator<SingleDocumentModel> iter = this.iterator();
		int index = 0;
		while (iter.hasNext()) {
			SingleDocumentModel doc = iter.next();
			if (doc.getFilePath() != null) {
				if (doc.getFilePath().equals(path)) {
					this.setSelectedIndex(index);
					return this.getDocument(index);
				}
			}
			index++;
		}

		SingleDocumentModel model = new DefaultSingleDocumentModel(path, readFile(path));
		notifyAddListeners(model);
		documents.add(model);

		this.add(this.getDocument(this.getNumberOfDocuments() - 1).getTextComponent());
		this.setSelectedIndex(this.getTabCount() - 1);
		this.setIconAt(this.getSelectedIndex(), saved);
		this.setTitleAt(this.getSelectedIndex(), path.getFileName().toString());
		this.setToolTipTextAt(this.getSelectedIndex(), path.toString());

		model.addSingleDocumentListener(new SingleDocumentListener() {

			@Override
			public void documentModifyStatusUpdated(SingleDocumentModel model) {
				if (model.isModified()) {
					DefaultMultipleDocumentModel.this.setIconAt(DefaultMultipleDocumentModel.this.getSelectedIndex(),
							notSaved);

					notifyChangeListeners();
				} else {
					DefaultMultipleDocumentModel.this.setIconAt(DefaultMultipleDocumentModel.this.getSelectedIndex(),
							saved);
				}

			}

			@Override
			public void documentFilePathUpdated(SingleDocumentModel model) {
				DefaultMultipleDocumentModel.this.setTitleAt(DefaultMultipleDocumentModel.this.getSelectedIndex(),
						model.getFilePath().getFileName().toString());

				DefaultMultipleDocumentModel.this.setToolTipTextAt(DefaultMultipleDocumentModel.this.getSelectedIndex(),
						model.getFilePath().toString());

			}

		});

		model.getTextComponent().addCaretListener(e -> {
			notifyChangeListeners();
		});

		model.getTextComponent().addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				notifyChangeListeners();

			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}

		});

		return model;
	}

	/**
	 * Method that notifies listeners about adding a new document to the multiple
	 * document model.
	 * 
	 * @param model
	 *            Document that is being added.
	 */
	private void notifyAddListeners(SingleDocumentModel model) {
		for (MultipleDocumentListener listener : this.listeners) {
			listener.documentAdded(model);
		}

	}

	/**
	 * Method that reads the file and transforms it into a string format.
	 * 
	 * @param path
	 *            Path of the file.
	 * @return String format of the file.
	 */
	private String readFile(Path path) {
		StringBuilder sb = new StringBuilder();

		try {
			List<String> lines = Files.readAllLines(path);
			int index = 0;
			for (String line : lines) {
				sb.append(line);
				if (index != lines.size() - 1) {
					sb.append("\n");
				}
				index++;
			}
		} catch (IOException el) {
			JOptionPane.showMessageDialog(this, "Error while opening file", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return sb.toString();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveDocument(SingleDocumentModel model, Path newPath) {
		if (!model.isModified() && !newPath.equals(model.getFilePath())) {
			return;
		}
		if (newPath == null) {
			newPath = model.getFilePath();
		}
		try {
			checkPath(newPath);
			writeToFile(model.getTextComponent().getText(), newPath);
			model.setModified(false);
			notifyChangeListeners();
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, "This file is opened in the editor!", "Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Method that checks if the file that "Save as.." is called on is already in
	 * the editor.
	 * 
	 * @param newPath
	 *            Path where the "Save as.." is called on.
	 */
	private void checkPath(Path newPath) {
		long count = documents.stream().filter(e -> e.getFilePath() != null && e.getFilePath().equals(newPath)).count();
		if (count > 1) {
			throw new IllegalArgumentException(
					"Location where this file should be saved has an opened document in the editor.");
		}

	}

	/**
	 * Method that writes the text to the file at the given path.
	 * 
	 * @param text
	 *            Text that is being written.
	 * @param newPath
	 *            Path where the text is written to.
	 */
	private void writeToFile(String text, Path newPath) {

		byte[] textByte = text.getBytes(StandardCharsets.UTF_8);
		try {
			Files.write(newPath, textByte);
		} catch (IOException el) {
			JOptionPane.showMessageDialog(this, "Error while saving file.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeDocument(SingleDocumentModel model) {
		documents.remove(model);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMultipleDocumentListener(MultipleDocumentListener l) {
		listeners.add(l);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMultipleDocumentListener(MultipleDocumentListener l) {
		listeners.remove(l);

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfDocuments() {
		return documents.size();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleDocumentModel getDocument(int index) {
		if (index < 0 || index >= this.getNumberOfDocuments()) {
			throw new IllegalArgumentException("Invalid index.");
		}
		return documents.get(index);
	}

}
