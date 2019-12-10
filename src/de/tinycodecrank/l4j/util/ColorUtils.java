package de.tinycodecrank.l4j.util;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

public class ColorUtils
{
	public static boolean isBright(Color color)
	{
		int aggregate = color.getBlue() + color.getGreen() + color.getRed();
		aggregate /= 3;
		return aggregate > 127;
	}
	
	public static ImageIcon selectResize(Color backgroundColor, Image light, Image dark, int width, int height)
	{
		final boolean isBright = isBright(backgroundColor);
		Image selected = isBright ? dark : light;
		
		return new ImageIcon(selected.getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}
}