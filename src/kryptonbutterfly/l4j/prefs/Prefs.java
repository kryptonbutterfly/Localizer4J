package kryptonbutterfly.l4j.prefs;

import java.awt.Frame;
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
	public GuiPrefs mainWindow = new GuiPrefs(100, 100, 810, 442, Frame.MAXIMIZED_BOTH);
	
	@Value
	public GuiPrefs settingsWindow = new GuiPrefs();
	
	@Value
	public GuiPrefs newLangWindow = new GuiPrefs(100, 100, 320, 110, Frame.NORMAL);
	
	@Value
	public GuiPrefs newProjectWindow = new GuiPrefs(100, 100, 550, 450, Frame.NORMAL);
	
	@Value
	public GuiPrefs searchWindow = new GuiPrefs(100, 100, 450, 300, Frame.NORMAL);
	
	@Value
	public FileSettings fileSettings = new FileSettings();
	
	@Value
	public History history = new History();
	
	@Value
	public MainWindowLayout layout = new MainWindowLayout();
	
	@Value
	public boolean pinnedOnTop = true;
	
	@Value
	public String language = null;
}