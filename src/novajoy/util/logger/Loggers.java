package novajoy.util.logger;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import novajoy.util.config.IniWorker;

public class Loggers {
	String janitorLog, crawlerLog, packerLog, senderLog, jdbcLog;
	SimpleFormatter formatter = new SimpleFormatter();

	public Loggers() {
		try {
			IniWorker config = new IniWorker(
                    "/home/ubuntu/NovaJoyConfig/config.ini");
			janitorLog = config.getJanitorLogsAddr();
			crawlerLog = config.getCrawlerLogsAddr();
			packerLog = config.getPackerLogsAddr();
			senderLog = config.getSenderLogsAddr();
			jdbcLog = config.getJdbcManagerLogsAddr();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Logger getLogger(String addr, String name) {
		Logger log = null;
		try {
			Handler fh;
			fh = new FileHandler(addr, true);
			fh.setFormatter(formatter);
			log = Logger.getLogger(name);
			log.addHandler(fh);
		} catch (Exception e) {
			System.out
					.println("Could not make logger for " + name
							+ " by address: " + addr + "\nException: "
							+ e.getMessage());
			log = Logger.getLogger(name + "_without_file_output");
		}
		return log;
	}

	public Logger getCrawlerLogger() {
		return getLogger(crawlerLog, "Crawler");
	}

	public Logger getJanitorLogger() {
		return getLogger(janitorLog, "Janitor");
	}

	public Logger getPackerLogger() {
		return getLogger(packerLog, "Packer");
	}

	public Logger getSenderLogger() {
		return getLogger(senderLog, "Sender");
	}

	public Logger getJdbcManagerLogger() {
		return getLogger(jdbcLog, "JdbcManager");
	}
}
