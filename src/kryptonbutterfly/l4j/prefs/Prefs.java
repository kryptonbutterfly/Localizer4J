package kryptonbutterfly.l4j.prefs;

import java.io.File;

import kryptonbutterfly.xmlConfig4J.FileConfig;
import kryptonbutterfly.xmlConfig4J.annotations.Value;

public class Prefs extends FileConfig
{
	public Prefs(File config)
	{
		super(config);
	}
	
	@Value
	public FileSettings fileSettings = new FileSettings();
	
	@Value
	public History history = new History();
	
	@Value
	public boolean pinnedOnTop = true;
	
	@Value
	public String language = null;
}