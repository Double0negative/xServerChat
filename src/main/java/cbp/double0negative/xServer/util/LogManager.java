package cbp.double0negative.xServer.util;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class LogManager
{
	private static LogManager _instance = new LogManager();
	private Logger log;
	private JavaPlugin p;
	private String pre = "";

	private LogManager()
	{
	}

	public void setup(JavaPlugin p)
	{
		this.p = p;
		log = p.getLogger();

	}

	public static LogManager getInstance()
	{
		return _instance;
	}

	public void info(String msg)
	{
		log.info(pre + msg);
	}

	public void warn(String msg)
	{
		log.warning(pre + msg);
	}

	public void error(String msg)
	{
		log.severe(pre + msg);
	}
}
