package novajoy.util.config;

import java.io.File;
import java.io.IOException;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class IniWorker {
	final Wini ini;

	public IniWorker(String ini_addr) throws InvalidFileFormatException,
			IOException {
		ini = new Wini(new File(ini_addr));
	}

	public String getDBaddress() {
		return ini.get("database", "address");
	}

	public String getDBbasename() {
		return ini.get("database", "base");
	}

	public String getDBuser() {
		return ini.get("database", "user");
	}

	public String getDBpassword() {
		return ini.get("database", "password");
	}

	public String getSenderSmtpHost() {
		return ini.get("sender", "smtphost");
	}

	public String getSenderSmtpPort() {
		return ini.get("sender", "smtpport");
	}

	public String getSenderFromMail() {
		return ini.get("sender", "from");
	}

	public String getSenderFromPass() {
		return ini.get("sender", "password");
	}

	public String getSenderSmtpUser() {
		return ini.get("sender", "smtpuser");
	}

	public String getSenderSmtpPass() {
		return ini.get("sender", "smtppass");
	}

	public int getCrawlerSleepTime() {
		return ini.get("crawler", "sleepmins", int.class);
	}

	public int getCrawlerThreads() {
		return ini.get("crawler", "threads", int.class);
	}

	public String getCrawlerLogsAddr() {
		return ini.get("logs", "crawler");
	}
	public String getSenderLogsAddr() {
		return ini.get("logs", "sender");
	}
	public String getPackerLogsAddr() {
		return ini.get("logs", "packer");
	}
	public String getJanitorLogsAddr() {
		return ini.get("logs", "janitor");
	}
	public String getJdbcManagerLogsAddr() {
		return ini.get("logs", "jdbc");
	}
}
