package kryptonbutterfly.l4j.misc;

import java.io.File;

import kryptonbutterfly.l4j.prefs.Prefs;
import kryptonbutterfly.l4j.prefs.State;
import kryptonbutterfly.os.BaseDirectory;
import kryptonbutterfly.os.Platforms;

public final class Globals
{
	private Globals()
	{}
	
	private static final String	CREATOR			= "kryptonbutterfly";
	private static final String	PROGRAM_NAME	= "localizer4j";
	
	private static final BaseDirectory BASE_DIR = Platforms.getOS().baseDir().init(CREATOR, PROGRAM_NAME);
	
	private static final File	PREFS_FILE	= new File(BASE_DIR.stateHome(), "preferences.xml");
	public static final File	LOG_FILE	= new File(BASE_DIR.stateHome(), "logs/log.log");
	private static final File	CONFIG_FILE	= new File(BASE_DIR.dataHome(), "config.xml");
	
	public static final State	windowStates	= new State(PREFS_FILE);
	public static final Prefs	prefs			= new Prefs(CONFIG_FILE);
}
