package kryptonbutterfly.l4j.prefs;

import java.io.File;

import kryptonbutterfly.l4j.util.Constants;
import kryptonbutterfly.util.swing.state.WindowState;
import kryptonbutterfly.util.swing.state.WindowState.ExtendedState;
import kryptonbutterfly.xmlConfig4J.FileConfig;
import kryptonbutterfly.xmlConfig4J.annotations.Value;

public class State extends FileConfig implements Constants
{
	public State(File config)
	{
		super(config);
	}
	
	@Value
	public WindowState mainWindow = new WindowState(100, 100, 810, 442, ExtendedState.MAXIMIZED_BOTH);
	
	@Value
	public WindowState settingsWindow = new WindowState();
	
	@Value
	public WindowState newLangWindow = new WindowState(100, 100, 320, 110);
	
	@Value
	public WindowState newProjectWindow = new WindowState(100, 100, 550, 450);
	
	@Value
	public WindowState searchWindow = new WindowState(100, 100, 450, 300);
	
	@Value
	public MainWindowLayout layout = new MainWindowLayout();
}
