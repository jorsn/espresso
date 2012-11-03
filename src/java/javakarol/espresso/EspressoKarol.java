/* EspressoKarol - a gui editor for javakarol
 * 
 * Copyright (C) 2011, Johannes Rosenberger <jo.rosenberger@gmx-topmail.de>
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

/**
 * 
 */
package javakarol.espresso;


import ro.jo.java.io.CharsetSensitiveFileToStringReader;

import java.awt.Image;
import java.awt.Font;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.StyledEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.syntaxkits.JavaSyntaxKit;
import jsyntaxpane.util.Configuration;

/**
 * @author Johannes Rosenberger
 * 
 */
public class EspressoKarol implements ActionListener, WindowListener {

	private Settings settings;

	HashMap<String, String> mode;

	JFrame frame;
	GridBagLayout layout;

	JTextArea modeDescTextArea;
	JEditorPane editor;
	JTextArea console;
	JTree tree;

	public EspressoKarol(String[] args) {
		this.settings = new Settings();
		argEval(args);

		initUI();
	}

	public void argEval(String[] args) {
		if (args.length > 0) {
			String version;
			String usage = "Usage: espresso [-v|--version]";
			if (!args[0].replace("--version", "-v").equals("-v"))
				version = new String();
			else
				version = settings.getVersion();

			int exitVal;
			if (!version.isEmpty()) {
				System.out.println(String.format("%n%1$s, version %2$s %n%3$s %n %n%4$s %n %n%5$s %n",
							settings.getName(), settings.getVersion(),
							settings.getLicenseHint(),
							settings.getBuildEnv(),
							usage));
				exitVal = 0;
			} else {
				System.err.println(usage);
				exitVal = 1;
			}
			System.exit(exitVal);
		}
	}

	public String getCleanEditorText() {
		//String auto = "// Auto code ";
		/*String cleanText = editor.getText().replace(auto + "begin\n"
				+ modeText + auto + "end\n", new String()).trim();*/
		/*String cleanText = Pattern.compile(auto + "begin.*"
				+ auto + "end", Pattern.DOTALL).
			matcher(editor.getText()).replaceAll(new String()).trim();*/
		String cleanText = Pattern.compile(String.format("%1$s begin.*%<s end", "// Auto code"), Pattern.DOTALL).
			matcher(editor.getText()).replaceAll(new String()).trim();
		return cleanText;
	}


	public void setEditorText() {
		try {
			/*String auto = "// Auto code ";
			String begin = auto + "begin\n";
			String end = auto + "end\n\n";*/

			String modeText = mode.get("text");
			/*String editorText = editor.getText();

			if (!editorText.contains(auto)) {
				if (!modeText.trim().isEmpty()) {
					editor.setText(begin + modeText + end + editorText);
				}
			} else {
				String toReplace = begin + oldModeText + end;
				if (modeText.trim().isEmpty()) {
					editor.setText(editorText.replace(toReplace, new String()).trim());
				} else {
					editor.setText(editorText.replace(toReplace, begin + modeText + end));
				}
			} */
			String editorText = getCleanEditorText();
			if (!modeText.isEmpty())
				editorText = String.format("%1$s begin\n%2$s%1$s end\n\n%3$s",
						"// Auto code", modeText, editorText);
				//editorText = begin + modeText + end + editorText;
			editor.setText(editorText);

		} catch (NullPointerException e) {
		}
	}

	public void initMode() {
		mode = settings.getMode(settings.getModeName());
		setEditorText();
		modeDescTextArea.setText(mode.get("desc"));
	}

	public FileFilter getFileFilter(final String description,
			final String regexName) {
		return new FileFilter() {

			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public boolean accept(File f) {
				return f.getName().matches(regexName);
			}
		};
	}

	public File chooseFile(int operation, FileFilter filter, String errorMsg, boolean changeTitle) {

		File file = new File(settings.getUserKarolDir());

		JFileChooser c = new JFileChooser(file.getParent());
		c.setCurrentDirectory(file);
		c.setFileFilter(filter);

		int returnVal = 42;
		if (operation == JFileChooser.OPEN_DIALOG) {
			returnVal = c.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = c.getSelectedFile();
				settings.setSegmentSavePath(file.getPath());
			}
		} else if (operation == JFileChooser.SAVE_DIALOG) {
			returnVal = c.showSaveDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = c.getSelectedFile();
				settings.setSegmentSavePath(file.getPath());
			}
		}

		if (returnVal == JFileChooser.CANCEL_OPTION)
			file = null;
		else if (returnVal == JFileChooser.ERROR_OPTION) {
			showError(errorMsg);
		}

		if (changeTitle)
			frame.setTitle(settings.getSegmentSavePath() + " - " + settings.getName());

		return file;
	}

	public File chooseFile(int operation, FileFilter filter, boolean changeTitle) {
		return chooseFile(operation, filter, "FEHLER: Datei nicht gefunden!", changeTitle);
	}

	public void writeFile(File file, String content) throws IOException {
		FileWriter w = new FileWriter(file);
		w.write(content);
		w.close();
	}

	public void writeFile(String path, String content) throws IOException {
		writeFile(new File(path), content);
	}

	public void openFile() {
		for (int i = 0; i < 3; i++) {
			try {
				File file = chooseFile(JFileChooser.OPEN_DIALOG,
						getFileFilter(
								"EspressoKarol java-Datei-Segmente (*.eks)",
								".*\\.eks"), true);
				if (file != null) {
					//editor.read(new FileReader(file), file.getPath());
					editor.setText(CharsetSensitiveFileToStringReader.readFile(file));
					setEditorText();
				}
				break;
			} catch (IOException e) {
				showError("Datei konnte nicht gelesen werden. Bitte neu auswählen.");
				e.printStackTrace();
			}
			if (i == 2)
				showError("FEHLER: Datei konnte zum dritten Mal nicht gelesen werden. Abbruch!");
		}
	}

	public void saveFile() {
		for (int i = 0; i < 3; i++) {
			try {
				File file = chooseFile(JFileChooser.SAVE_DIALOG,
						getFileFilter(
								"EspressoKarol java-Datei-Segmente (*.eks)",
								".*.\\.eks"), true);
				String overw = "Datei " + file.getName() + " überschreiben?";
				if (file != null && (!file.exists()
							|| JOptionPane.showConfirmDialog(frame, overw,
								overw + " - EspressoKarol",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
					/*FileWriter w = new FileWriter(file);
					w.write(getCleanEditorText());
					w.close();*/
					writeFile(file, getCleanEditorText());
				}
				break;
			} catch (IOException e) {
				showError("Datei konnte nicht geschrieben werden. Bitte neu auswählen.");
				e.printStackTrace();
			}
			if (i == 2)
				showError("FEHLER: Datei konnte zum dritten Mal nicht geschrieben werden. Abbruch!");
		}
	}

	public void exportAsJavaFile(File javafile, String content)
			throws IOException {
		/*
		 * File template = new File(ClassLoader.getSystemResource(
		 * getClass().getPackage().getName().replace(".", File.separator) +
		 * File.separator + mode).getPath());
		 */
	
		if (!javafile.getParentFile().exists())
			javafile.getParentFile().mkdirs();

		writeFile(javafile, mode.get("code").replaceFirst("Player",
					javafile.getName().split("\\.")[0]).replace(
						"// karolcode", content));
		/*FileWriter writer = new FileWriter(javafile);
		writer.write(mode.get("code").replaceFirst("Player",
				javafile.getName().split("\\.")[0]).replace("// karolcode", content));
		writer.close();*/
	}

	public void play() throws IOException {
		String userKarolDir = settings.getUserKarolDir();
		File javafile = new File(String.format("%1$s%3$s%2$s%3$sPlayer" + Integer.toHexString(editor.getText().hashCode())
					+ ".java", userKarolDir, settings.getPackageDir(), File.separator));
		exportAsJavaFile(javafile, editor.getText());
		String jklibdir = String.format((settings.isLocal() ? "..%1$slib"
					: "..%1$s..%<slib%<sjava%<sjavakarol"),
				File.separator);
		String classpathString = String.format("%1$s%4$s%2$s%3$sjavakarol.jar%4$s.",
				userKarolDir, jklibdir, File.separator, File.pathSeparator);
			/*userKarolDir + File.pathSeparator + "."
				+ File.pathSeparator + (settings.isLocal() ? ".."
						+ File.separator + "lib" : ".." + File.separator
						+ ".." + File.separator + "lib" + File.separator
						+ "java" + File.separator
						+ "javakarol") + File.separator + "javakarol.jar";*/
		ProcessBuilder compilation = new ProcessBuilder(settings.getJavac(), "-classpath",
				classpathString, javafile.getAbsolutePath());
		Process compp = compilation.start();
		console.read(new BufferedReader(new InputStreamReader(
					new SequenceInputStream(
						compp.getErrorStream(),
						compp.getInputStream()))),
				"espresso-karol-javac-output");
		try {
			if (compp.waitFor() == 0) {
				Process karolexec = new ProcessBuilder(settings.getJava(),
								"-classpath", classpathString, getClass()
										.getPackage().getName()
										+ "."
										+ javafile.getName().split("\\.")[0])
								.start();
				console.read(new BufferedReader(new InputStreamReader(
							new SequenceInputStream(
								karolexec.getErrorStream(),
								karolexec.getInputStream()))),
						"espresso-karol-java-output");
								
			}
		} catch (InterruptedException e) {
			console.read(new StringReader(e.getMessage()),
					"InterruptedException while running java");
		}
		javafile.deleteOnExit();
		new File(javafile.getAbsolutePath().replace(".java", ".class"))
				.deleteOnExit();
	}

	public void actionPerformed(ActionEvent e) {
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String command = e.getActionCommand();
		if (command.equals("save")) {
			saveFile();
		} else if (command.equals("open")) {
			openFile();
		} else if (command.equals("play")) {
			try {
				play();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (command.startsWith("lookAndFeel=")) {
			setLookAndFeel(command.split("=")[1]);
		} else if (command.equals("setJavacLoc")) {
			String javac = chooseFile(JFileChooser.OPEN_DIALOG,
					getFileFilter("Java Compiler", "javac(\\..*)?"),
					"FEHLER: Der Pfad ist ungültig!\n"
					+ "Verwende bisherigen Pfad: " + settings.getJavac(), false)
				.getAbsolutePath();
			if (javac != null)
				settings.setJavac(javac);
		} else if (command.equals("setJavaLoc")) {
			String java = chooseFile(JFileChooser.OPEN_DIALOG,
					getFileFilter("Java VM", "java(\\..*)?"),
					"FEHLER: Der Pfad ist ungültig!\n"
					+ "Verwende bisherigen Pfad: " + settings.getJava(), false)
				.getAbsolutePath();
			if (java != null)
				settings.setJava(java);
		} else if (command.startsWith("mode=")) {
			settings.setMode(command.split("=")[1]);
			initMode();
		} else if (command.equals("about")) {
			AboutDialog dialog = new AboutDialog(settings);
		}/* else if (command.equals("copy")) {
			editor.copy();
		} else if (command.equals("cut")) {
			editor.cut();
		} else if (command.equals("paste")) {
			editor.paste();
		}*/
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void setLookAndFeel(String newLookAndFeel) {
		try {
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			Class<?> lookClass = cl.loadClass(newLookAndFeel);
			UIManager.setLookAndFeel((LookAndFeel) lookClass.getConstructor()
					.newInstance());
			SwingUtilities.updateComponentTreeUI(frame);
			settings.setLookAndFeel(newLookAndFeel);
		} catch (Exception e) {
			if (new File(settings.getUserConfigFile()).exists())
				showError("Design konnte nicht geladen werden. Benutze Standard.");
			setLookAndFeel(UIManager
					.getSystemLookAndFeelClassName());
		}
	}

	public void addGridBagComponent(Container cont, GridBagLayout gbl,
			Component c, int x, int y, int width, int height, double weightx,
			double weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}

	public void showError(String message) {
		JOptionPane.showMessageDialog(frame, message, "FEHLER",
				JOptionPane.ERROR_MESSAGE);
		System.err.println(String.format("%1$s: %2$s", settings.getName(), message));
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		int exit = JOptionPane.showConfirmDialog(frame,
				settings.getName() + " beenden?", "Beenden",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (exit == JOptionPane.YES_OPTION) {
			int eksSave = JOptionPane.showConfirmDialog(frame,
					"Vor dem Verlassen speichern?", "Speichern",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (eksSave == JOptionPane.YES_OPTION)
				saveFile();

			if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH)
				settings.setMaximized();
			else
				settings.setDefaultSize(frame.getSize());

			String saveErr = settings.save();
			if (!saveErr.isEmpty()) {
				int exitAnw = JOptionPane.showConfirmDialog(frame,
						String.format("Speichern der Konfiguration"
							+ "fehlgeschlagen:\n%1s\n\nTrotzdem beenden?",
							saveErr), "Beenden",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (exitAnw == JOptionPane.YES_OPTION)
					System.exit(2);
			} else {
				System.exit(0);
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		if (JOptionPane.showConfirmDialog(frame, "Datei öffnen?", "Öffnen",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
			openFile();
	}

	public void initUI() {
		frame = new JFrame();
		layout = new GridBagLayout();

		Configuration config = DefaultSyntaxKit.getConfig(JavaSyntaxKit.class);
		config.put("Components", "jsyntaxpane.components.PairsMarker,"
				+ "jsyntaxpane.components.TokenMarker");
		config.put("PopupMenu", "cut-to-clipboard,copy-to-clipboard,"
				+ "paste-from-clipboard,-,select-all,-,undo,redo,"
				+ "-,find,find-next,-,indent,unindent");
		editor = new JEditorPane();

		console = new JTextArea();

		frame.addWindowListener(this);

		frame.setLayout(layout);
		frame.setTitle(settings.getSegmentSavePath() + " - " + settings.getName());
		frame.setIconImage(settings.getIcon());
		setLookAndFeel(settings.getLookAndFeel());

		Dimension defaultSize = settings.getDefaultSize();
		if (defaultSize == null) {
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			frame.setSize(defaultSize);
		}
		frame.setMinimumSize(new Dimension(400, 300));

		/*StyledEditorKit editorkit = new StyledEditorKit();
		editorkit.install(editor);

		JMenuItem copy = new JMenuItem("Kopieren");
		copy.setActionCommand("copy");
		JMenuItem cut = new JMenuItem("Ausschneiden");
		cut.setActionCommand("cut");
		JMenuItem paste = new JMenuItem("Einfügen");
		paste.setActionCommand("paste");

		final JPopupMenu pmenu = new JPopupMenu();
		for (JMenuItem item : new JMenuItem[] { copy, cut, paste }) {
			pmenu.add(item);
			item.addActionListener(this);
		}

		editor.add(pmenu);
		editor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				maybeShowPopupMenu(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopupMenu(e);
			}

			public void maybeShowPopupMenu(MouseEvent e) {
				if (e.isPopupTrigger()) {
					pmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});*/
		modeDescTextArea = new JTextArea();
		modeDescTextArea.setEditable(false);
		modeDescTextArea.setBackground(Color.LIGHT_GRAY);
		modeDescTextArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));



		DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				WELT.class.getPackage());
		Class<?>[] karolclasses = { WELT.class, ROBOTER.class };
		for (Class<?> karolclass : karolclasses) {
			String[] classname = karolclass.getName().split("\\.");
			DefaultMutableTreeNode node = new DefaultMutableTreeNode("class "
					+ classname[classname.length - 1]);
			DefaultMutableTreeNode constructors = new DefaultMutableTreeNode(
					"Konstruktoren");
			DefaultMutableTreeNode methods = new DefaultMutableTreeNode(
					"Methoden");

			for (Constructor<?> constructor : karolclass
					.getDeclaredConstructors()) {
				if (Modifier.isPublic(constructor.getModifiers())) {
					String params = new String();
					try {
						params = constructor.toGenericString().split("\\(")[1]
								.split("\\)")[0];
					} catch (ArrayIndexOutOfBoundsException e) {
					} finally {
						String[] constructor_name = constructor.getName()
								.split("\\.");
						constructors.add(new DefaultMutableTreeNode(
								constructor_name[constructor_name.length - 1]
										+ "(" + params + ")"));
					}
				}
			}
			for (Method method : karolclass.getDeclaredMethods()) {
				if (Modifier.isPublic(method.getModifiers())) {
					String params = new String();
					try {
						params = method.toGenericString().split("\\(")[1]
								.split("\\)")[0];
					} catch (ArrayIndexOutOfBoundsException e) {
					} finally {
						methods.add(new DefaultMutableTreeNode(method.getName()
								+ "(" + params + ")" + " : "
								+ method.getReturnType()));
					}
				}
			}

			for (DefaultMutableTreeNode subnode : new DefaultMutableTreeNode[] {
					constructors, methods }) {
				if (subnode.getChildCount() > 0)
					node.add(subnode);
			}
			root.add(node);
		}

		tree = new JTree(root);
		tree.setRootVisible(true);

		JButton saveButton = new JButton(new ImageIcon("icons/save.png"));
		saveButton.setToolTipText("Speichern");
		saveButton.setActionCommand("save");

		JButton openButton = new JButton(new ImageIcon("icons/open.png"));
		openButton.setToolTipText("Datei öffnen");
		openButton.setActionCommand("open");

		JButton playButton = new JButton(new ImageIcon("icons/play.png"));
		playButton.setToolTipText("Abspielen");
		playButton.setActionCommand("play");

		JMenu settingsMenu = new JMenu("Einstellungen");

		JMenu lookAndFeelMenu = new JMenu("Design auswählen");
		for (LookAndFeelInfo l : UIManager.getInstalledLookAndFeels()) {
			JMenuItem item = new JMenuItem(l.getName());
			item.setActionCommand("lookAndFeel=" + l.getClassName());
			item.addActionListener(this);
			lookAndFeelMenu.add(item);
		}

		JMenu modeMenu = new JMenu("Modus auswählen");
		for (Object modeo : settings.getModes().keySet().toArray()) {
			String availMode = (String) modeo;
			JMenuItem item = new JMenuItem(availMode);

			String desc = settings.getMode(availMode).get("desc");
			if (desc.isEmpty())
				desc = "Beschreibung nicht verfügbar";

			item.setToolTipText(desc);

			item.setActionCommand("mode=" + availMode);
			item.addActionListener(this);
			modeMenu.add(item);
		}

		JMenuItem setJavacLoc = new JMenuItem(
				"Pfad für den Java Compiler setzen");
		setJavacLoc.addActionListener(this);
		setJavacLoc.setActionCommand("setJavacLoc");

		JMenuItem setJavaLoc = new JMenuItem("Pfad für die Java VM setzen");
		setJavaLoc.addActionListener(this);
		setJavaLoc.setActionCommand("setJavaLoc");

		settingsMenu.add(lookAndFeelMenu);
		settingsMenu.add(modeMenu);
		settingsMenu.add(setJavaLoc);
		settingsMenu.add(setJavacLoc);

		JMenu helpMenu = new JMenu("Hilfe");

		JMenuItem about = new JMenuItem("Über "
				+ this.getClass().getSimpleName());
		about.addActionListener(this);
		about.setActionCommand("about");

		helpMenu.add(about);

		JMenuBar bar = new JMenuBar();

		bar.add(openButton);
		bar.add(saveButton);
		bar.add(playButton);
		bar.add(settingsMenu);
		bar.add(helpMenu);

		for (Component c : bar.getComponents()) {
			if (c.getClass().getSimpleName().equals("JButton")) {
				JButton b = (JButton) c;
				b.addActionListener(this);
				b.setContentAreaFilled(false);
			}
		}

		editor.setMinimumSize(new Dimension(200, 100));
		console.setMinimumSize(new Dimension(200, 100));
		console.setEditable(false);
		tree.setMinimumSize(new Dimension(50, 100));

		JPanel editP = new JPanel(true);
		editP.setLayout(layout);

		addGridBagComponent(editP, layout, modeDescTextArea, 0, 50, 800, 50, 0, 0);
		addGridBagComponent(editP, layout, new JScrollPane(editor), 0,
				GridBagConstraints.RELATIVE, 800, GridBagConstraints.RELATIVE,
				1.0, 1.0);

		JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		right.setBottomComponent(new JScrollPane(console));
		right.setTopComponent(editP);
		right.setDividerLocation(frame.getHeight() * 2 / 3);
		right.setOneTouchExpandable(true);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				new JScrollPane(tree), right);
		split.setDividerLocation(frame.getWidth() / 3);
		split.setOneTouchExpandable(true);

		frame.setJMenuBar(bar);
		//addGridBagComponent(frame, layout, bar, 0, 50, 800, 50, 0, 0);
		addGridBagComponent(frame, layout, split, 0,
				GridBagConstraints.RELATIVE, 800, GridBagConstraints.RELATIVE,
				1.0, 1.0);

		DefaultSyntaxKit.initKit();
		editor.setContentType("text/java");
		initMode();

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		EspressoKarol espresso = new EspressoKarol(args);
	}

}

// vim: foldmethod=syntax
