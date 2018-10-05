package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.swing.FormLocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.swing.LocalizableAction;

/**
 * This is a class that represents a Notepad++. Notepad++ is a text editor that
 * can store multiple documents in its tabbed window. Notepad++ also supports
 * usage of various action that modify the documents uploaded/created in the
 * editor. The program also supports localization for English, German and
 * Croatian language.
 * 
 * @author Dinz
 *
 */
public class JNotepadPP extends JFrame {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -1828031542060447668L;

	/**
	 * Multiple document model used for handling the documents in the Notepad++.
	 */
	private DefaultMultipleDocumentModel model = new DefaultMultipleDocumentModel();

	/**
	 * Internal clipboard in the program.
	 */
	private String clipBoard = null;

	/**
	 * Form localization provider.
	 */
	private FormLocalizationProvider flp = new FormLocalizationProvider(LocalizationProvider.getInstance(), this);

	/**
	 * File menu.
	 */
	JMenu fileMenu = new JMenu("File");

	/**
	 * Edit menu.
	 */
	JMenu editMenu = new JMenu("Edit");

	/**
	 * Info menu.
	 */
	JMenu infoMenu = new JMenu("Info");

	/**
	 * Tools menu.
	 */
	JMenu tools = new JMenu("Tools");

	/**
	 * Languages menu.
	 */
	JMenu languageMenu = new JMenu("Languages");

	/**
	 * Change case submenu.
	 */
	JMenu chCase = new JMenu("Change case");

	/**
	 * Sort submenu.
	 */
	JMenu sort = new JMenu("Sort");

	/**
	 * Panel which wraps the elements and is fixed in the center of the program
	 * layout.
	 */
	JPanel panel = new JPanel(new BorderLayout());

	/**
	 * Length label.
	 */
	JLabel length = new JLabel();

	/**
	 * Position label.
	 */
	JLabel position = new JLabel();

	/**
	 * Label that tracks date and time.
	 */
	JLabel dateTime = new JLabel();

	/**
	 * Date instance.
	 */
	Date date = new Date();

	/**
	 * Main method that runs the Notepad++.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new JNotepadPP().setVisible(true);
		});

	}

	/**
	 * Constructs a new JNotepadPP class.
	 */
	public JNotepadPP() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (checkSaved()) {
					int reply = JOptionPane.showConfirmDialog(JNotepadPP.this,
							LocalizationProvider.getInstance().getString("exit_save_changes_dialog"));
					if (reply == JOptionPane.YES_OPTION) {
						saveModifiedDocuments();
						JNotepadPP.this.dispose();
						date.stop();
					} else if (reply == JOptionPane.NO_OPTION) {
						JNotepadPP.this.dispose();
						date.stop();
					} else {
						return;
					}
				} else {
					JNotepadPP.this.dispose();
					date.stop();
				}

			}

		});
		setLocation(0, 0);
		setSize(800, 600);
		setTitle("JNotepad++");
		initGUI();
	}

	/**
	 * Method that initializes the GUI for the Notepad++.
	 */
	private void initGUI() {
		panel.add(new JScrollPane(model), BorderLayout.CENTER);
		getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panel, BorderLayout.CENTER);

		model.addMultipleDocumentListener(new MultipleDocumentListener() {

			@Override
			public void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel) {

				if (currentModel == null) {
					JNotepadPP.this.setTitle("JNotepad++");
					JNotepadPP.this.length
							.setText("  " + LocalizationProvider.getInstance().getString("length") + ": ");
					JNotepadPP.this.position.setText(LocalizationProvider.getInstance().getString("line") + ": "
							+ LocalizationProvider.getInstance().getString("column") + ": "
							+ LocalizationProvider.getInstance().getString("selection") + ": ");
					return;
				}

				JNotepadPP.this.length.setText("  " + LocalizationProvider.getInstance().getString("length") + ": "
						+ currentModel.getTextComponent().getText().length());
				JNotepadPP.this.position.setText(LocalizationProvider.getInstance().getString("line") + ": "
						+ calculateLine(currentModel) + " " + LocalizationProvider.getInstance().getString("column")
						+ ": " + calculateColumn(currentModel) + " "
						+ LocalizationProvider.getInstance().getString("selection") + " "
						+ calculateSelected(currentModel));
				if (currentModel.getFilePath() == null) {

					return;
				}
				JNotepadPP.this.setTitle("JNotepad++ - " + currentModel.getFilePath().toString());

			}

			/**
			 * Calculates the current line position of the caret in the opened document.
			 * 
			 * @param currentModel
			 *            Currently opened document.
			 * @return Line position of the caret.
			 */
			private String calculateLine(SingleDocumentModel currentModel) {
				try {
					int row = currentModel.getTextComponent()
							.getLineOfOffset(currentModel.getTextComponent().getCaretPosition());
					return Integer.toString(row + 1);
				} catch (BadLocationException e) {

				}
				return null;
			}

			/**
			 * Calculates the current column position of the caret in the opened document.
			 * 
			 * @param currentModel
			 *            Currently opened document.
			 * @return Column position of the caret.
			 */
			private String calculateColumn(SingleDocumentModel currentModel) {
				int column;
				try {
					column = currentModel.getTextComponent().getCaretPosition()
							- currentModel.getTextComponent().getLineStartOffset(currentModel.getTextComponent()
									.getLineOfOffset(currentModel.getTextComponent().getCaretPosition()));
					return Integer.toString(column + 1);
				} catch (BadLocationException e) {

				}
				return null;

			}

			/**
			 * Calculates the number of characters currently selected.
			 * 
			 * @param currentModel
			 *            Currently opened document.
			 * @return Number of characters currently selected.
			 */
			private String calculateSelected(SingleDocumentModel currentModel) {
				int selected = currentModel.getTextComponent().getCaret().getDot()
						- currentModel.getTextComponent().getCaret().getMark();
				return Integer.toString(Math.abs(selected));
			}

			@Override
			public void documentAdded(SingleDocumentModel model) {
				// TODO Auto-generated method stub

			}

			@Override
			public void documentRemoved(SingleDocumentModel model) {
				// TODO Auto-generated method stub

			}

		});

		createActions();
		createMenus();
		createToolbars();
	}

	/**
	 * Method that starts the action of asking user if he wants to save currently
	 * modified documents when he chose to close the Notepad++.
	 */
	private void saveModifiedDocuments() {
		Iterator<SingleDocumentModel> iter = model.iterator();
		int index = 0;
		while (iter.hasNext()) {
			SingleDocumentModel doc = iter.next();
			model.setSelectedIndex(index);
			if (doc.isModified()) {
				int reply = JOptionPane.showConfirmDialog(JNotepadPP.this,
						LocalizationProvider.getInstance().getString("save_dialog"),
						LocalizationProvider.getInstance().getString("save_q_dialog"), JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					saveDocumentAction.actionPerformed(null);
				} else {
				}
			}
			index++;
		}

	}

	/**
	 * Method that creates toolbars in Notepad++.
	 */
	private void createToolbars() {
		JToolBar toolBar = new JToolBar("Tools");

		toolBar.add(new JButton(createDocumentAction));
		toolBar.add(new JButton(openDocumentAction));
		toolBar.add(new JButton(saveDocumentAction));
		toolBar.add(new JButton(saveDocumentAsAction));
		toolBar.add(new JButton(closeDocumentAction));
		toolBar.add(new JButton(cutAction));
		toolBar.add(new JButton(copyAction));
		toolBar.add(new JButton(pasteAction));
		toolBar.add(new JButton(infoAction));

		JPanel statsBar = new JPanel();
		statsBar.setLayout(new BorderLayout());

		length.setText("  length: ");
		length.setHorizontalAlignment(JLabel.LEFT);

		position.setText("Ln:  Col:  Sel:");
		position.setHorizontalAlignment(JLabel.CENTER);

		dateTime.add(date);

		statsBar.add(length, BorderLayout.WEST);
		statsBar.add(position, BorderLayout.CENTER);
		statsBar.add(dateTime, BorderLayout.EAST);

		this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
		panel.add(statsBar, BorderLayout.PAGE_END);

	}

	/**
	 * Method that creates menus in Notepad++.
	 */
	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();

		menuBar.add(fileMenu);

		fileMenu.add(new JMenuItem(createDocumentAction));
		fileMenu.add(new JMenuItem(openDocumentAction));
		fileMenu.add(new JMenuItem(saveDocumentAction));
		fileMenu.add(new JMenuItem(saveDocumentAsAction));
		fileMenu.add(new JMenuItem(closeDocumentAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(exitAction));

		menuBar.add(editMenu);

		editMenu.add(new JMenuItem(cutAction));
		editMenu.add(new JMenuItem(copyAction));
		editMenu.add(new JMenuItem(pasteAction));

		menuBar.add(infoMenu);

		infoMenu.add(new JMenuItem(infoAction));

		tools.add(chCase);
		chCase.add(new JMenuItem(toUpperCaseAction));
		chCase.add(new JMenuItem(toLowerCaseAction));
		chCase.add(new JMenuItem(switchCaseAction));

		tools.add(sort);
		sort.add(new JMenuItem(ascendingAction));
		sort.add(new JMenuItem(descendingAction));
		sort.add(new JMenuItem(uniqueAction));
		menuBar.add(tools);

		languageMenu.add(new JMenuItem(englishAction));
		languageMenu.add(new JMenuItem(croatianAction));
		languageMenu.add(new JMenuItem(germanAction));
		menuBar.add(languageMenu);

		this.setJMenuBar(menuBar);
	}

	/**
	 * Method that initialize actions in Notepad++ with its description, mnemonic
	 * key and accelerator key.
	 */
	private void createActions() {
		openDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
		openDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		openDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Used to open file from disk");

		createDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
		createDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		createDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Creates a new document");

		saveDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
		saveDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		saveDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Saves the current document");

		saveDocumentAsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control R"));
		saveDocumentAsAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
		saveDocumentAsAction.putValue(Action.SHORT_DESCRIPTION, "Saves the document at a desired location");

		closeDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control L"));
		closeDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		closeDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Closes current document");

		cutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 1"));
		cutAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		cutAction.putValue(Action.SHORT_DESCRIPTION, "Deletes selected text and stores it into a clipboard");

		copyAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 2"));
		copyAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_K);
		copyAction.putValue(Action.SHORT_DESCRIPTION, "Copies selected text to the clipboard.");

		pasteAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 3"));
		pasteAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
		pasteAction.putValue(Action.SHORT_DESCRIPTION, "Pastes the text from the clipboard to selected position");

		infoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control I"));
		infoAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
		infoAction.putValue(Action.SHORT_DESCRIPTION, "Presents an info about the current document");

		exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Q"));
		exitAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
		exitAction.putValue(Action.SHORT_DESCRIPTION, "Exit Notepad++");

		toUpperCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control U"));
		toUpperCaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
		toUpperCaseAction.putValue(Action.SHORT_DESCRIPTION, "Switch selected text to upper case.");

		toLowerCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control W"));
		toLowerCaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
		toLowerCaseAction.putValue(Action.SHORT_DESCRIPTION, "Switch selected text to lower case.");

		switchCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control T"));
		switchCaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
		switchCaseAction.putValue(Action.SHORT_DESCRIPTION, "Switch case of selected text.");

		ascendingAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control B"));
		ascendingAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
		ascendingAction.putValue(Action.SHORT_DESCRIPTION, "Ascendingly sort lines");

		descendingAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control G"));
		descendingAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
		descendingAction.putValue(Action.SHORT_DESCRIPTION, "Descendingly sort lines");

		uniqueAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control U"));
		uniqueAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
		uniqueAction.putValue(Action.SHORT_DESCRIPTION, "Erase duplicate lines from selected ones");

	}

	/**
	 * Action which loads a new document to the editor from the disk.
	 */
	private final Action openDocumentAction = new LocalizableAction("open", flp) {

		/**
		 * Serial
		 */
		private static final long serialVersionUID = -3660225573128456868L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Open file");
			if (fc.showOpenDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File fileName = fc.getSelectedFile();
			Path filePath = fileName.toPath();

			if (!Files.isReadable(filePath)) {
				JOptionPane.showMessageDialog(JNotepadPP.this,
						LocalizationProvider.getInstance().getString("file") + fileName.getAbsolutePath()
								+ LocalizationProvider.getInstance().getString("does_not_exist"),
						LocalizationProvider.getInstance().getString("file_not_found"), JOptionPane.ERROR_MESSAGE);
				return;
			}

			model.loadDocument(filePath);

		}

	};

	/**
	 * Exit action, an action which exits Notepad++. If there is any unsaved
	 * documents opened in the editor, this action asks the user if he wants to save
	 * those files.
	 */
	private Action exitAction = new LocalizableAction("exit", flp) {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2796634880921047137L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (checkSaved()) {
				int reply = JOptionPane.showConfirmDialog(JNotepadPP.this,
						LocalizationProvider.getInstance().getString("exit_save_changes_dialog"));
				if (reply == JOptionPane.YES_OPTION) {
					saveModifiedDocuments();
					JNotepadPP.this.dispose();
					date.stop();
				} else if (reply == JOptionPane.NO_OPTION) {
					JNotepadPP.this.dispose();
					date.stop();
				} else {
					return;
				}
			} else {
				JNotepadPP.this.dispose();
				date.stop();
			}

			JNotepadPP.this.dispose();
			date.stop();

		}

	};

	/**
	 * Action that represents creating a new blank document in the editor.
	 */
	private final Action createDocumentAction = new LocalizableAction("create", flp) {

		/**
		 * Serial
		 */
		private static final long serialVersionUID = -3330931000709851504L;

		@Override
		public void actionPerformed(ActionEvent e) {
			model.createNewDocument();

		}

	};

	/**
	 * Action that represents cutting selected text - Deleting selected text from
	 * the editor and storing it into a clipboard.
	 */
	private Action cutAction = new LocalizableAction("cut", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = 6366520240770468350L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}
			JTextArea editor = (JTextArea) model.getSelectedComponent();
			Document doc = editor.getDocument();

			int len = Math.abs(editor.getCaret().getDot() - editor.getCaret().getMark());

			if (len == 0)
				return;

			int offset = Math.min(editor.getCaret().getDot(), editor.getCaret().getMark());

			try {
				JNotepadPP.this.clipBoard = doc.getText(offset, len);
				doc.remove(offset, len);
			} catch (BadLocationException el) {
				el.printStackTrace();
			}
		}

	};

	/**
	 * Action that represents copying selected text - Storing selected text into the
	 * clipboard.
	 */
	private Action copyAction = new LocalizableAction("copy", flp) {

		/**
		 * Serial
		 */
		private static final long serialVersionUID = -7990560770170460604L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}
			JTextArea editor = (JTextArea) model.getSelectedComponent();
			Document doc = editor.getDocument();

			int len = Math.abs(editor.getCaret().getDot() - editor.getCaret().getMark());

			if (len == 0)
				return;

			int offset = Math.min(editor.getCaret().getDot(), editor.getCaret().getMark());

			try {
				JNotepadPP.this.clipBoard = doc.getText(offset, len);
			} catch (BadLocationException el) {
				el.printStackTrace();
			}
		}

	};

	/**
	 * Action that represents pasting text from the clipboard into the currently set
	 * position.
	 */
	private Action pasteAction = new LocalizableAction("paste", flp) {

		/**
		 * Serial
		 */
		private static final long serialVersionUID = 70178771341626582L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}
			JTextArea editor = (JTextArea) model.getSelectedComponent();
			Document doc = editor.getDocument();

			int len = Math.abs(editor.getCaret().getDot() - editor.getCaret().getMark());

			int offset = Math.min(editor.getCaret().getDot(), editor.getCaret().getMark());

			try {
				doc.remove(offset, len);
				doc.insertString(offset, clipBoard, null);
			} catch (BadLocationException el) {
				el.printStackTrace();
			}
		}

	};

	/**
	 * Action that presents statistical information to the user about the character
	 * count of the current document.
	 */
	private Action infoAction = new LocalizableAction("info", flp) {

		/**
		 * Serial
		 */
		private static final long serialVersionUID = 70178771341626582L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}
			JTextArea editor = (JTextArea) model.getSelectedComponent();
			String text = editor.getText();

			int fullLength = text.length();
			int nonBlankLength = text.replaceAll("\\s+", "").length();
			int lineCount = countLines(text);

			JOptionPane.showMessageDialog(JNotepadPP.this,
					LocalizationProvider.getInstance().getString("your_document_has") + " " + fullLength + " "
							+ LocalizationProvider.getInstance().getString("characters") + " " + nonBlankLength + " "
							+ LocalizationProvider.getInstance().getString("non_blank_characters") + " " + lineCount
							+ " " + LocalizationProvider.getInstance().getString("lines"),
					LocalizationProvider.getInstance().getString("statistical_information"),
					JOptionPane.INFORMATION_MESSAGE);
		}

	};

	/**
	 * Action that saves the current document to it's already known path. If there
	 * is no known path, the user has to choose the location on the disk where the
	 * file should be saved.
	 */
	private Action saveDocumentAction = new LocalizableAction("save", flp) {

		/**
		 * Serial
		 */
		private static final long serialVersionUID = 70178771341626582L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}
			int index = model.getSelectedIndex();
			SingleDocumentModel doc = model.getDocument(index);
			if (doc.getFilePath() == null) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Save file as");
				if (fc.showSaveDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
					return;
				}

				File fileName = fc.getSelectedFile();
				Path filePath = fileName.toPath();

				if (Files.exists(filePath)) {
					int reply = JOptionPane.showConfirmDialog(JNotepadPP.this,
							LocalizationProvider.getInstance().getString("file_exists_dialog"),
							LocalizationProvider.getInstance().getString("overwrite_file"), JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.YES_OPTION) {

					} else {
						return;
					}
				}

				doc.setFilePath(filePath);
			}
			model.saveDocument(doc, model.getDocument(index).getFilePath());
		}

	};

	/**
	 * Action that saves the currently opened document to the desired location on
	 * the disk.
	 */
	private Action saveDocumentAsAction = new LocalizableAction("save_as", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = 70178771341626582L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}
			int index = model.getSelectedIndex();
			SingleDocumentModel doc = model.getDocument(index);

			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Save file as");
			if (fc.showSaveDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File fileName = fc.getSelectedFile();
			Path filePath = fileName.toPath();

			if (Files.exists(filePath)) {
				int reply = JOptionPane.showConfirmDialog(JNotepadPP.this,
						LocalizationProvider.getInstance().getString("file_exists_dialog"),
						LocalizationProvider.getInstance().getString("overwrite_file"), JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {

				} else {
					return;
				}
			}

			doc.setFilePath(filePath);
			model.saveDocument(doc, doc.getFilePath());
		}

	};

	/**
	 * Action which closes currently opened document in the editor. The user is
	 * asked if he really wants to close the document before actual termination.
	 */
	private Action closeDocumentAction = new LocalizableAction("close", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = 70178771341626582L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}

			int reply = JOptionPane.showConfirmDialog(JNotepadPP.this,
					LocalizationProvider.getInstance().getString("close_document_dialog"),
					LocalizationProvider.getInstance().getString("close_document"), JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {

			} else {
				return;
			}

			int index = model.getSelectedIndex();
			model.closeDocument(model.getDocument(index));
			model.removeTabAt(index);

		}

	};

	/**
	 * Action that transforms selected text to uppercase.
	 */
	private Action toUpperCaseAction = new LocalizableAction("upper", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = 8561091307169087482L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}
			int index = model.getSelectedIndex();
			SingleDocumentModel doc = model.getDocument(index);

			int len = Math
					.abs(doc.getTextComponent().getCaret().getDot() - doc.getTextComponent().getCaret().getMark());

			if (len == 0)
				return;

			int offset = Math.min(doc.getTextComponent().getCaret().getDot(),
					doc.getTextComponent().getCaret().getMark());
			changeCase(offset, len, Casing.UPPERCASE, doc);
		}

	};

	/**
	 * Action that transforms selected text to lowercase.
	 */
	private Action toLowerCaseAction = new LocalizableAction("lower", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = 8561091307169087482L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}
			int index = model.getSelectedIndex();
			SingleDocumentModel doc = model.getDocument(index);

			int len = Math
					.abs(doc.getTextComponent().getCaret().getDot() - doc.getTextComponent().getCaret().getMark());

			if (len == 0)
				return;

			int offset = Math.min(doc.getTextComponent().getCaret().getDot(),
					doc.getTextComponent().getCaret().getMark());
			changeCase(offset, len, Casing.LOWERCASE, doc);
		}

	};

	/**
	 * Action that inverts the case of currently selected text.
	 */
	private Action switchCaseAction = new LocalizableAction("switch", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = 8561091307169087482L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}
			int index = model.getSelectedIndex();
			SingleDocumentModel doc = model.getDocument(index);

			int len = Math
					.abs(doc.getTextComponent().getCaret().getDot() - doc.getTextComponent().getCaret().getMark());

			if (len == 0)
				return;

			int offset = Math.min(doc.getTextComponent().getCaret().getDot(),
					doc.getTextComponent().getCaret().getMark());
			changeCase(offset, len, Casing.SWITCHCASE, doc);
		}

	};

	/**
	 * Action that sets the language of Notepad++ to Croatian.
	 */
	private Action croatianAction = new LocalizableAction("croatian", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = 1039196291708262397L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LocalizationProvider.getInstance().setLanguage("hr");
			localizationChanged();
			model.notifyChangeListeners();
		}

	};

	/**
	 * Action that sets the language of Notepad++ to English.
	 */
	private Action englishAction = new LocalizableAction("english", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = -2320654903477302660L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LocalizationProvider.getInstance().setLanguage("en");
			localizationChanged();
			model.notifyChangeListeners();
		}

	};

	/**
	 * Action that sets the language of Notepad++ to German.
	 */
	private Action germanAction = new LocalizableAction("german", flp) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1444795640905414262L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LocalizationProvider.getInstance().setLanguage("de");
			localizationChanged();
			model.notifyChangeListeners();
		}

	};

	/**
	 * Action that takes lines of selected text and does the ascending sort.
	 */
	private Action ascendingAction = new LocalizableAction("ascending", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = 2944907531926574005L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}

			int index = model.getSelectedIndex();
			SingleDocumentModel doc = model.getDocument(index);

			int len = Math
					.abs(doc.getTextComponent().getCaret().getDot() - doc.getTextComponent().getCaret().getMark());
			if (len == 0)
				return;

			int offset = Math.min(doc.getTextComponent().getCaret().getDot(),
					doc.getTextComponent().getCaret().getMark());
			int selected = doc.getTextComponent().getCaret().getDot() - doc.getTextComponent().getCaret().getMark();

			try {
				sorting(doc, offset, selected, Sorting.ASCENDING);

			} catch (BadLocationException e) {

			}
		}

	};

	/**
	 * Method that executes sorting based on the sorting type.
	 * 
	 * @param doc
	 *            Currently opened document.
	 * @param offset
	 *            offset.
	 * @param selected
	 *            Length of the selected text.
	 * @param sorting
	 *            Sorting type
	 * @throws BadLocationException
	 */
	private void sorting(SingleDocumentModel doc, int offset, int selected, Sorting sorting)
			throws BadLocationException {
		int line = doc.getTextComponent().getLineOfOffset(offset);
		int fromPos = doc.getTextComponent().getLineStartOffset(line);
		int lineEnd = doc.getTextComponent().getLineOfOffset(fromPos + Math.abs(selected));
		int toPos = doc.getTextComponent().getLineEndOffset(lineEnd);
		String text = doc.getTextComponent().getText(fromPos, toPos - fromPos);
		doc.getTextComponent().getDocument().remove(fromPos, toPos - fromPos);

		String[] split = text.split("\n");
		if (split.length < 2) {
			return;
		}
		List<String> lista = new ArrayList<>();
		for (String str : split) {
			lista.add(str);
		}

		Locale locale = new Locale(LocalizationProvider.getInstance().getLanguage());
		Collator collator = Collator.getInstance(locale);

		if (sorting == Sorting.ASCENDING) {
			lista = lista.stream().sorted((s1, s2) -> collator.compare(s1, s2)).collect(Collectors.toList());
		} else if (sorting == Sorting.DESCENDING) {
			lista = lista.stream().sorted((s1, s2) -> collator.compare(s2, s1)).collect(Collectors.toList());
		} else if (sorting == Sorting.UNIQUE) {
			lista = lista.stream().distinct().collect(Collectors.toList());
		}

		StringBuilder sb = new StringBuilder();
		for (String str : lista) {
			sb.append(str + "\n");
		}

		doc.getTextComponent().getDocument().insertString(fromPos, sb.toString(), null);
	}

	/**
	 * Action that takes lines of the selected text and does the descending sort.
	 */
	private Action descendingAction = new LocalizableAction("descending", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = 9153176092472419306L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}

			int index = model.getSelectedIndex();
			SingleDocumentModel doc = model.getDocument(index);

			int len = Math
					.abs(doc.getTextComponent().getCaret().getDot() - doc.getTextComponent().getCaret().getMark());
			if (len == 0)
				return;

			int offset = Math.min(doc.getTextComponent().getCaret().getDot(),
					doc.getTextComponent().getCaret().getMark());
			int selected = doc.getTextComponent().getCaret().getDot() - doc.getTextComponent().getCaret().getMark();

			try {
				sorting(doc, offset, selected, Sorting.DESCENDING);

			} catch (BadLocationException e) {

			}
		}

	};

	/**
	 * Action that removes duplicate lines from the selected text.
	 */
	private Action uniqueAction = new LocalizableAction("unique", flp) {

		/**
		 * Serial.
		 */
		private static final long serialVersionUID = -3713619067814308819L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!validation()) {
				return;
			}

			int index = model.getSelectedIndex();
			SingleDocumentModel doc = model.getDocument(index);

			int len = Math
					.abs(doc.getTextComponent().getCaret().getDot() - doc.getTextComponent().getCaret().getMark());
			if (len == 0)
				return;

			int offset = Math.min(doc.getTextComponent().getCaret().getDot(),
					doc.getTextComponent().getCaret().getMark());
			int selected = doc.getTextComponent().getCaret().getDot() - doc.getTextComponent().getCaret().getMark();

			try {
				sorting(doc, offset, selected, Sorting.UNIQUE);

			} catch (BadLocationException e) {

			}
		}

	};

	/**
	 * Method that is called when there is a change in the language of Notepad++. It
	 * then renames all the menus and descriptions of actions in the editor.
	 */
	private void localizationChanged() {
		fileMenu.setText(LocalizationProvider.getInstance().getString("file"));
		editMenu.setText(LocalizationProvider.getInstance().getString("edit"));
		infoMenu.setText(LocalizationProvider.getInstance().getString("info"));
		tools.setText(LocalizationProvider.getInstance().getString("tools"));
		languageMenu.setText(LocalizationProvider.getInstance().getString("language"));
		chCase.setText(LocalizationProvider.getInstance().getString("change_case"));
		sort.setText(LocalizationProvider.getInstance().getString("sort"));

		openDocumentAction.putValue(Action.SHORT_DESCRIPTION,
				LocalizationProvider.getInstance().getString("open_descr"));
		createDocumentAction.putValue(Action.SHORT_DESCRIPTION,
				LocalizationProvider.getInstance().getString("create_descr"));
		saveDocumentAction.putValue(Action.SHORT_DESCRIPTION,
				LocalizationProvider.getInstance().getString("save_descr"));
		saveDocumentAsAction.putValue(Action.SHORT_DESCRIPTION,
				LocalizationProvider.getInstance().getString("save_as_descr"));
		closeDocumentAction.putValue(Action.SHORT_DESCRIPTION,
				LocalizationProvider.getInstance().getString("close_descr"));
		cutAction.putValue(Action.SHORT_DESCRIPTION, LocalizationProvider.getInstance().getString("cut_descr"));
		copyAction.putValue(Action.SHORT_DESCRIPTION, LocalizationProvider.getInstance().getString("copy_descr"));
		pasteAction.putValue(Action.SHORT_DESCRIPTION, LocalizationProvider.getInstance().getString("paste_descr"));
		infoAction.putValue(Action.SHORT_DESCRIPTION, LocalizationProvider.getInstance().getString("info_descr"));
		exitAction.putValue(Action.SHORT_DESCRIPTION, LocalizationProvider.getInstance().getString("exit_descr"));
		toUpperCaseAction.putValue(Action.SHORT_DESCRIPTION,
				LocalizationProvider.getInstance().getString("upper_descr"));
		toLowerCaseAction.putValue(Action.SHORT_DESCRIPTION,
				LocalizationProvider.getInstance().getString("lower_descr"));
		switchCaseAction.putValue(Action.SHORT_DESCRIPTION,
				LocalizationProvider.getInstance().getString("switch_descr"));
		ascendingAction.putValue(Action.SHORT_DESCRIPTION,
				LocalizationProvider.getInstance().getString("ascending_descr"));
		descendingAction.putValue(Action.SHORT_DESCRIPTION,
				LocalizationProvider.getInstance().getString("descending_descr"));
		uniqueAction.putValue(Action.SHORT_DESCRIPTION, LocalizationProvider.getInstance().getString("unique_descr"));

	}

	/**
	 * Method that disables buttons if there is no documents in the editor.
	 * 
	 * @return False if there is no documents in the editor, true otherwise.
	 */
	private boolean validation() {
		if (model.getNumberOfDocuments() < 1) {
			return false;
		}
		return true;
	}

	/**
	 * Method that changes the case of selected text.
	 * 
	 * @param offset
	 *            Offset.
	 * @param len
	 *            Length of the selected text.
	 * @param casing
	 *            Casing type.
	 * @param doc
	 *            Currently opened document.
	 */
	private void changeCase(int offset, int len, Casing casing, SingleDocumentModel doc) {
		try {
			String text = doc.getTextComponent().getText(offset, len);
			if (casing == Casing.UPPERCASE) {
				text = text.toUpperCase();
			} else if (casing == Casing.LOWERCASE) {
				text = text.toLowerCase();
			} else if (casing == Casing.SWITCHCASE) {
				text = switchCase(text);
			}
			doc.getTextComponent().getDocument().remove(offset, len);
			doc.getTextComponent().getDocument().insertString(offset, text, null);
		} catch (BadLocationException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Method that switches case in the given text.
	 * 
	 * @param text
	 *            Given text.
	 * @return Text with inverted casing.
	 */
	private String switchCase(String text) {
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (Character.isLowerCase(c)) {
				chars[i] = Character.toUpperCase(c);
			} else if (Character.isUpperCase(c)) {
				chars[i] = Character.toLowerCase(c);
			}
		}
		return new String(chars);
	}

	/**
	 * Method that counts the lines of the given string.
	 * 
	 * @param str
	 *            Given text.
	 * @return Number of lines.
	 */
	private int countLines(String str) {
		String[] lines = str.split("\r\n|\r|\n");
		return lines.length;
	}

	/**
	 * Method that checks if there is any unsaved documents in Notepad++
	 * 
	 * @return True if there is any unsaved documents in the editor, false
	 *         otherwise.
	 */
	private boolean checkSaved() {
		Iterator<SingleDocumentModel> iter = model.iterator();
		while (iter.hasNext()) {
			SingleDocumentModel doc = iter.next();
			if (doc.isModified()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Class that represents the date that is being shown in the editor.
	 * 
	 * @author Dinz
	 *
	 */
	class Date extends JComponent {

		/**
		 * Serial
		 */
		private static final long serialVersionUID = -4911096263154230961L;

		/**
		 * Date and time in the form of the string.
		 */
		volatile String dateTime;

		/**
		 * Flag that determines if the date updater should be stopped or not.
		 */
		volatile boolean stop;

		/**
		 * Date formatter.
		 */
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

		/**
		 * Time formatter.
		 */
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

		/**
		 * Constructs a new date model.
		 */
		public Date() {
			updateTime();

			Thread t = new Thread(() -> {
				while (true) {
					try {
						Thread.sleep(500);
					} catch (Exception ex) {
					}
					if (stop)
						break;
					SwingUtilities.invokeLater(() -> {
						updateTime();
					});
				}

			});
			t.setDaemon(true);
			t.start();
		}

		/**
		 * Updates the date and time.
		 */
		private void updateTime() {
			String time = timeFormatter.format(LocalTime.now());
			String date = dateFormatter.format(LocalDate.now());

			dateTime = date + " " + time;
			JNotepadPP.this.dateTime.setText(dateTime + " ");
		}

		/**
		 * Terminates the class.
		 */
		public void stop() {
			stop = true;
		}

	}

}