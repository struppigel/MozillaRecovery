package model;

public enum Application {

	THUNDERBIRD(".thunderbird", "Thunderbird"), 
	FIREFOX(".mozilla/firefox", "Mozilla/Firefox");

	private String linuxDir;
	private String windowsDir;

	private Application(String linuxDir, String windowsDir) {
		this.linuxDir = linuxDir;
		this.windowsDir = windowsDir;
	}

	public String getLinuxDir() {
		return linuxDir;
	}

	public String getWindowsDir() {
		return windowsDir;
	}
}
