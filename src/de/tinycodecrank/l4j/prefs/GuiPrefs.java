package de.tinycodecrank.l4j.prefs;

import java.awt.Component;
import java.awt.Frame;

import de.tinycodecrank.xmlConfig4J.annotations.Value;

public class GuiPrefs
{
	public GuiPrefs(){}
	
	public GuiPrefs(int posX, int posY, int width, int height, int state)
	{
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.state = state;
	}
	
	@Value
	public int posX = 100;
	
	@Value
	public int posY = 100;
	
	@Value
	public int width = 800;
	
	@Value
	public int height = 600;
	
	@Value
	public int state = Frame.NORMAL;
	
	public void setBounds(Component target)
	{
		target.setBounds(posX, posY, width, height);
	}
	
	public void setBoundsAndState(Frame target)
	{
		target.setBounds(posX, posY, width, height);
		target.setExtendedState(state);
	}
}