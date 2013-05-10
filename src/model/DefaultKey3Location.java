package model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

public class DefaultKey3Location {

	private final static Logger logger = Logger
			.getLogger(DefaultKey3Location.class);
	private final static String PROFILES_INI = "profiles.ini";
	private final static String SEP = System.getProperty("file.separator");

	public String findLocation(Application appType) {
		String appDir = getAppDataDir(appType);
		logger.debug("application dir: " + appDir);
		String profileIni = appDir + SEP + PROFILES_INI;
		String defaultProfile = getDefaultProfile(profileIni);
		logger.debug("defaultProfile" + defaultProfile);

		if (defaultProfile != null) {
			String location = appDir + SEP + defaultProfile + SEP + "key3.db";
			if (new File(location).exists()) {
				return location;
			}
		}
		return null;
	}

	private String getDefaultProfile(String profileIni) {
		List<String> lines;
		try {
			lines = FileIO.readFile(profileIni);
		if (lines != null) {
			for (String line : lines) {
				if (line.contains("default") && line.contains("Path")) {
					return line.split("=")[1];
				}
			}
		}
		} catch (IOException e) {
			logger.warn("unable to read profile.ini " + e.getMessage());
		}
		return null;
	}

	private String getAppDataDir(Application appType) {
		File appDataDir;
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			appDataDir = new File(System.getProperty("user.home"),
					"AppData\\Roaming\\" + appType.getWindowsDir());
		} else {
			appDataDir = new File(System.getProperty("user.home"), appType.getLinuxDir());
		}

		if (appDataDir.exists()) {
			return appDataDir.getPath();
		}
		return null;
	}

}
