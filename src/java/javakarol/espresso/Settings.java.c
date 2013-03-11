package javakarol.espresso;


import ro.jo.java.io.CharsetSensitiveFileToStringReader;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Image;

class Settings {
	final static int OS_TYPE_WINDOWS = 0;
	final static int OS_TYPE_UNIX = 42;

	final static String NAME = __EK_NAME__;
	final static String VERSION = __EK_VERSION__;
	private final static String LICENSE_HINT = __EK_LICENSE_HINT__;
	private final static String BUILD_ENV = __EK_BUILD_ENV__;

	private String gloprops;
	private Properties props;

	private HashMap<String, HashMap<String, String>> modes;
	final static String DEFAULT_MODE = "default";

	private Image icon;

	protected Settings () {
		initProps();

		icon = Toolkit.getDefaultToolkit().getImage(getIconPath());
		initModes();
	}

	protected void initProps() {
		// reader used in this method
		BufferedReader reader;

		// main property file stored in env var
		gloprops = System.getenv("EK_PROPS");

		props = readProps(props, gloprops);

		// load additional props specified in main prop file
		for (String key : props.keySet().toArray(new String[0])) {
			String val = getEkProp(key);
			if ((key.startsWith("ek.props.static") || key.startsWith("eks.props.static")) && new File(val).exists())
				props = readProps(props, val);
		}

		String saves = getEkProp("ek.props.saves");
		if (new File(saves).exists())
				props = readProps(props, saves);
	}

	protected Properties readProps(Properties defaultProps, String path) {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		Properties props = new Properties(defaultProps);
		props.load(reader);

		return props;
	}

	protected String getEkProp(String key) {
		String eks = key.replaceAll("^ek\\.", "eks\\.");
		String ek = key.replaceAll("^eks\\.", "ek\\.");
		
		String val = props.getProperty(eks);
		if (val.isEmpty())
			val = props.getProperty(ek);

		return val;
	}

	protected boolean isLocal() {
		return getEkProp("ek.islocal").equals("0");
	}

	protected String getPackageDir() {
		return getEkProp("ek.packagedir");
	}

	protected void setPackageDir(String packageDir) {
		props.setProperty("ek.packagedir", packageDir);
	}

	protected String getUserKarolDir() {
		return getEkProp("ek.user.dir");
	}
	
	protected void setUserKarolDir(String userKarolDir) {
		props.setProperty("ek.user.dir", userKarolDir);
	}

	protected String getDataDir() {
		return getEkProp("ek.datadir");
	}

	protected String getModesDir() {
		return getDataDir() + File.separator + "modes";
	}

	protected String getIconPath() {
		return String.format("%2s%1sicons%1sespresso.ico"
				, File.separator
				, getDataDir());
	}

	protected Image getIcon() {
		return icon;
	}

	protected String getLicensePath() {
		return getEkProp("ek.licenses.paths.ek");
	}

	protected String getMplPath() {
		return getEkProp("ek.licenses.paths.mpl");
	}

	protected String getApacheLicensePath() {
		return getEkProp("ek.licenses.paths.apache");
	}

	protected String getPropSavesPath() {
		return getEkProp("ek.props.saves");
	}

	protected String getSegmentSavePath() {
		return getEkProp("ek.segmentsavepath");
	}

	protected void setSegmentSavePath(String segmentSavePath) {
		props.setProperty("ek.segmentsavepath", segmentSavePath);
	}

	protected Dimension getDefaultSize() {
		Dimension defaultSize = null;
		String[] size = getEkProp("ek.size").split("x");
		if (size.length == 2) {
			int width = Integer.parseInt(size[0]);
			int height = Integer.parseInt(size[1]);
			defaultSize = new Dimension(width, height);
		}

		return defaultSize;
	}

	protected void setDefaultSize(double width, double height) {
		props.setProperty("ek.size", new Double(width).intValue() + "x" + new Double(height).intValue());
	}

	protected void setDefaultSize(Dimension size) {
		setDefaultSize(size.getWidth(), size.getHeight());
	}

	protected void setMaximized() {
		props.setProperty("ek.size", "max");
	}

	protected HashMap<String, HashMap<String, String>> getModes() {
		return modes;
	}

	protected String getModeName() {
		return getEkProp("ek.modes.default");
	}

	protected HashMap<String, String> getMode(String name) {
		return modes.get(name);
	}

	protected void setMode(String name) {
		props.setProperty("ek.modes.default", name);
	}

	protected void removeResetMode(String mode) {
		modes.remove(mode);
		mode = Settings.DEFAULT_MODE;
		initMode(mode);
	}

	protected HashMap<String, String> initMode(String mode) {
		HashMap<String, String> map = new HashMap<String, String>(3, 1f);
		File modeDir = new File(getModesDir(), mode);
		if (!modeDir.isDirectory()) {
			if (modeDir.exists())
				System.err.println(name + ": FATAL ERROR: "
						+ modeDir.getAbsolutePath() + " is not a directory!");
			else
				System.err.println(name + ": FATAL ERROR: The directory"
						+ modeDir.getAbsolutePath() + " does not exist!");

			removeResetMode(mode);

		} else {
			for (String partFileName : new String[] { "code", "text", "desc" }) {
				try {
					/*File partFile = new File(modeDir.getAbsolutePath()
							+ File.separator + partFileName);
					FileReader reader = new FileReader(partFile);
					char[] cbuf = new char[(int) partFile.length()];

					reader.read(cbuf);
					mode.put(partFileName, new String(cbuf));*/
					map.put(partFileName, CharsetSensitiveFileToStringReader.readFile(modeDir.getAbsolutePath()
							+ File.separator + partFileName));

				} catch (Exception e) {
					if (partFileName.equals("code")) {
						removeResetMode(mode);
						e.printStackTrace();
					} else {
						map.put(partFileName, new String());
					}
				}
			}
			if (map.get("desc").isEmpty())
				map.put("desc", "Für diesen Modus ist keine Beschreibung verfügbar.");
			//setEditorText();
			//modeDescTextArea.setText(mode.get("desc"));
		}

		return map;
	}

	protected void initModes() {
		String[] modearr = new File(getModesDir()).list();
		modes = new HashMap<String, HashMap<String, String>>(modearr.length);
		for (Object modeO : modearr) {
			String mode = (String) modeO;
			modes.put(mode, null);
			modes.put(mode, initMode(mode));
		}
	}

	protected String getLookAndFeel() {
		return getEkProp("ek.lookandfeel");
	}

	protected void setLookAndFeel(String name) {
		props.setProperty("ek.lookandfeel", name);
	}

	protected void save() {
		Properties toSaveProps = new Properties();
		for (String key : props.keySet().toArray(new String[0]))
			if (props.getProperty(key.replaceAll("^ek\\.", "eks\\."), new String()).isEmpty())
				toSaveProps.setProperty(key, props.getProperty(key));

		BufferedWriter writer = new BufferedWriter(new FileWriter(getEkProp("ek.props.saves")));
		toSaveProps.store(writer, "Saved Properties. Changes will be overwritten.");
	}

}

// vim: foldmethod=syntax filetype=java
