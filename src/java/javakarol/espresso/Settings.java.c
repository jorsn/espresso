package javakarol.espresso;


import ro.jo.java.io.CharsetSensitiveFileToStringReader;

import net.jmatrix.eproperties.EProperties;

import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JFrame;

class Settings {
	final static int OS_TYPE_WINDOWS = 0;
	final static int OS_TYPE_UNIX = 42;

	final static String NAME = __EK_NAME__;
	final static String VERSION = __EK_VERSION__;
	final static String LICENSE_HINT = __EK_LICENSE_HINT__;
	final static String BUILD_ENV = __EK_BUILD_ENV__;

	private String gloprops;
	private EProperties props;

	private HashMap<String, HashMap<String, String>> modes;
	final static String DEFAULT_MODE = "default";

	private Image icon;

	protected Settings () {
		initProps();

		icon = Toolkit.getDefaultToolkit().getImage(getIconPath());
		initModes();
	}

	protected void initProps() {
		props = new EProperties();
		// main property file stored in env var
		gloprops = System.getenv("EK_PROPS");

		try {
			props.load(gloprops);
		} catch (FileNotFoundException e) {
			System.err.println("FATAL ERROR: main property file(" + gloprops + ") not found:");
			e.printStackTrace();
			System.exit(2);
		} catch (IOException e) {
			System.err.println("FATAL ERROR: main property file(" + gloprops + ") could not be read:");
			e.printStackTrace();
			System.exit(2);
		}

	}

	protected String getEkProp(String key) {
		String eks = key.replaceAll("^ek\\.", "eks\\.");
		String ek = key.replaceAll("^eks\\.", "ek\\.");
		
		String val = props.getProperty(eks);
		if (val == null || val.isEmpty()) {
			val = props.getProperty(ek);
			if (val == null || val.isEmpty())
				val = new String();
		}

		return val;
	}

	protected boolean isLocal() {
		return getEkProp("ek.islocal").equals("true");
	}

	protected String getPackageDir() {
		return getEkProp("ek.packagedir");
	}

	protected void setPackageDir(String packageDir) {
		props.setProperty("ek.packagedir", packageDir);
	}

	protected String getUserKarolDir() {
		return getEkProp("ek.userdir");
	}
	
	protected void setUserKarolDir(String userKarolDir) {
		props.setProperty("ek.userdir", userKarolDir);
	}

	protected String getDataDir() {
		return getEkProp("ek.datadir");
	}

	protected String getModesDir() {
		return getDataDir() + File.separator + "modes";
	}

	protected String getIconDir() {
		return getDataDir() + File.separator + "icons";
	}

	protected String getIconPath() {
		return getIconDir() + File.separator + "espresso.png";
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
		String ekSize = getEkProp("ek.size");
		int width;
		int height;
		if (ekSize.isEmpty()) {
			width = 800;
			height = 600;
			
		} else {
			String[] size = ekSize.split("x");
			width = Integer.parseInt(size[0]);
			height = Integer.parseInt(size[1]);
		}
		return new Dimension(width, height);
	}

	protected void setDefaultSize(double width, double height) {
		props.setProperty("ek.size", new Double(width).intValue() + "x" + new Double(height).intValue());
	}

	protected void setDefaultSize(Dimension size) {
		setDefaultSize(size.getWidth(), size.getHeight());
	}

	protected void setExtendedState(int state) {
		props.setProperty("ek.extendedState", Integer.toString(state));
	}

	protected int getExtendedState() {
		String extStateS = getEkProp("ek.extendedState");
		return extStateS.isEmpty()
			? JFrame.NORMAL
			: Integer.parseInt(extStateS);
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
				System.err.println(NAME + ": FATAL ERROR: "
						+ modeDir.getAbsolutePath() + " is not a directory!");
			else
				System.err.println(NAME + ": FATAL ERROR: The directory"
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
					map.put("offset", Integer.toString(map.get("code").split("// karolcode")[0].split("\\n").length - 1 ));
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

	protected void save() throws IOException {
		EProperties toSaveProps = new EProperties();
		for (Object okey : props.keySet().toArray()) {
			String key = (String) okey;
			if (key.startsWith("ek") && props.getProperty(key.replaceAll("^ek\\.", "eks\\."), new String()).isEmpty())
				toSaveProps.setProperty(key, props.getProperty(key));
		}

		toSaveProps.save(getEkProp("ek.props.saves"));
	}

	protected String getJava() {
		String java = getEkProp("ek.java.vm");
		if (java == null || java.isEmpty())
			java = String.format("%1$s%2$sbin%2$sjava",
					System.getProperty("java.home"), File.separator);
		return java;
	}
	
	// be removed
	protected String getJavac() {
		return getEkProp("ek.java.compiler");
	}

}

// vim: foldmethod=syntax filetype=java
