package javakarol.espresso;


import ro.jo.java.io.CharsetSensitiveFileToStringReader;

import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Image;

class Settings {
	final static int OS_TYPE_WINDOWS = 0;
	final static int OS_TYPE_UNIX = 42;

	private String name;
	private String version;
	private String licenseHint;
	private String buildEnv;

	private Map<String, String> env;

	private HashMap<String, HashMap<String, String>> modes;
	final static String DEFAULT_MODE = "standard";

	private Image icon;

	protected Settings () {
		name = BuildInfo.NAME;
		version = BuildInfo.VERSION;
		licenseHint = String.format(BuildInfo.LICENSE_HINT); //, getLinefeed());
		buildEnv = String.format(BuildInfo.BUILD_ENV);

		env = new HashMap<String, String>(System.getenv());
		for (String prefix : new String[] { "EK", "EKP" }) {
			env.put(prefix + "_OS_TYPE_WINDOWS", Integer.toString(OS_TYPE_WINDOWS));
			env.put(prefix + "_OS_TYPE_UNIX", Integer.toString(OS_TYPE_UNIX));
			env.put(prefix + "_OS_TYPE", Integer.toString(File.separator.equals("/")
					? OS_TYPE_UNIX
					: OS_TYPE_WINDOWS));
		}

		icon = Toolkit.getDefaultToolkit().getImage(getIconPath());
		initModes();
	}

	protected int getOs() {
		return Integer.parseInt(env.get("EK_OS_TYPE"));
	}

	protected boolean isLocal() {
		return env.get("EK_IS_LOCAL").equals("0");
	}

	protected String getLinefeed() {
		return System.getProperty("line.separator", "\n");
	}

	protected Map<String, String> getEnv() {
		return env;
	}

	protected String getEnv(String envVarname) {
		return env.get(envVarname);
	}

	protected String getName() {
		return name;
	}

	protected String getVersion() {
		return version;
	}

	protected String getLicenseHint() {
		return licenseHint;
	}

	protected String getBuildEnv() {
		return buildEnv;
	}

	protected String getPackageDir() {
		return env.get("EK_PACKAGE_DIR");
	}

	protected void setPackageDir(String packageDir) {
		env.put("EK_PACKAGE_DIR", packageDir);
	}

	protected String getUserPackageDir() {
		return env.get("EK_USER_PACKAE_DIR");
	}

	protected void setUserPackageDir(String userPackageDir) {
		env.put("EK_USER_PACKAE_DIR", userPackageDir);
	}

	protected String getHomeDir() {
		return env.get("EK_HOME_DIR");
	}

	protected String getUserKarolDir() {
		return env.get("EK_USER_DIR");
	}
	
	protected void setUserKarolDir(String userKarolDir) {
		env.put("EK_USER_DIR", userKarolDir);
	}

	protected String getProfilesDir() {
		return env.get("EK_PROFILES_DIR");
	}

	protected void setProfilesDir(String profilesDir) {
		env.put("EK_PROFILES_DIR", profilesDir);
	}

	protected String getIconPath() {
		return env.get("EK_ICON_PATH");
	}

	protected Image getIcon() {
		return icon;
	}

	protected String getLicensePath() {
		return env.get("EK_LICENSE_PATH");
	}

	protected String getMplPath() {
		return env.get("EK_MPL_PATH");
	}

	protected String getApacheLicensePath() {
		return env.get("EK_APACHE_LICENSE_PATH");
	}

	protected String getSystemConfigFile() {
		return env.get("EK_SYSTEM_CONFIG_FILE");
	}

	protected String getUserConfigFile() {
		return env.get("EK_USER_CONFIG_FILE");
	}

	protected String getSegmentSavePath() {
		return env.get("EK_SEGMENT_SAVE_PATH");
	}

	protected void setSegmentSavePath(String segmentSavePath) {
		env.put("EK_SEGMENT_SAVE_PATH", segmentSavePath);
	}

	protected String getJavac() {
		return env.get("EK_JAVAC");
	}

	protected void setJavac(String javac) {
		env.put("EK_JAVAC", javac);
	}

	protected String getJava() {
		return env.get("EK_JAVA");
	}

	protected void setJava(String java) {
		env.put("EK_JAVA", java);
	}

	protected Dimension getDefaultSize() {
		Dimension defaultSize = null;
		String[] size = env.get("EK_SIZE").split("x");
		if (size.length == 2) {
			int width = Integer.parseInt(size[0]);
			int height = Integer.parseInt(size[1]);
			defaultSize = new Dimension(width, height);
		}

		return defaultSize;
	}

	protected void setDefaultSize(double width, double height) {
		env.put("EK_SIZE", new Double(width).intValue() + "x" + new Double(height).intValue());
	}

	protected void setDefaultSize(Dimension size) {
		setDefaultSize(size.getWidth(), size.getHeight());
	}

	protected void setMaximized() {
		env.put("EK_SIZE", "max");
	}

	protected String getSaveScriptName() {
		return env.get("EK_SAVE_SCRIPT_NAME") + "." + env.get("EK_SCRIPT_TYPE_" + (getOs() == OS_TYPE_UNIX ? "UNIX" : "DOS"));
	}

	protected HashMap<String, HashMap<String, String>> getModes() {
		return modes;
	}

	protected String getModeName() {
		return env.get("EK_MODE_NAME");
	}

	protected HashMap<String, String> getMode(String name) {
		return modes.get(name);
	}

	protected void setMode(String name) {
		env.put("EK_MODE_NAME", name);
	}

	protected void removeResetMode(String mode) {
		modes.remove(mode);
		mode = Settings.DEFAULT_MODE;
		initMode(mode);
	}

	protected HashMap<String, String> initMode(String mode) {
		HashMap<String, String> map = new HashMap<String, String>(3, 1f);
		File modeDir = new File(getProfilesDir() + File.separator + mode);
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
		String[] modearr = env.get("EK_MODES").split("\\s");
		modes = new HashMap<String, HashMap<String, String>>(modearr.length);
		for (Object modeO : modearr) {
			String mode = (String) modeO;
			modes.put(mode, null);
			modes.put(mode, initMode(mode));
		}
	}

	protected String getLookAndFeel() {
		return env.get("EK_LOOK_AND_FEEL");
	}

	protected void setLookAndFeel(String name) {
		env.put("EK_LOOK_AND_FEEL", name);
	}

	protected String save() {
		String saveErrs = null;
		try {
			ProcessBuilder savepb = new ProcessBuilder(getSaveScriptName());
			Map<String, String> saveEnv = savepb.environment();
			saveEnv.clear();
			saveEnv.putAll(env);
			Process savep = savepb.start();
			InputStream stderr = savep.getErrorStream();
			byte[] b = new byte[stderr.available()];
			stderr.read(b);

			saveErrs = new String(b);
		} catch (IOException e) {
			String error = getName() + ": FATAL ERROR: config could not be saved";
			e.printStackTrace();
			System.err.println(error);
			saveErrs = error;
		}

		return saveErrs;
	}

}

// vim: foldmethod=syntax
