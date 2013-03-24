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
}
