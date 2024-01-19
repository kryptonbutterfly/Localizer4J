package kryptonbutterfly.l4j.misc;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import kryptonbutterfly.monads.opt.Opt;

public final class Assets
{
	public static final String APPLICATION_NAME = "Localizer4J";
	
	public static final String	ICON_FILE_NAME	= "icon2.png";
	public static final String	SVG_ICON_FILE	= "icon.svg";
	public static final String	ASSETS_PACKAGE	= "/assets/";
	public static final String	ASSETS_LANGS	= ASSETS_PACKAGE + "lang/";
	
	public static final String	ICON_PINNED_LIGHT	= "pinned-light.png";
	public static final String	ICON_PINNED_DARK	= "pinned-dark.png";
	public static final String	ICON_UNPINNED_LIGHT	= "unpinned-light.png";
	public static final String	ICON_UNPINNED_DARK	= "unpinned-dark.png";
	
	private static Opt<Image> loadImage(String image)
	{
		try (InputStream iStream = Assets.class.getResourceAsStream(image))
		{
			return Opt.of(ImageIO.read(iStream));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return Opt.empty();
		}
	}
	
	private static final Image	MISSING_IMAGE_LIGHT	= createMissingImage(true);
	private static final Image	MISSING_IMAGE_DARK	= createMissingImage(false);
	
	public static final Opt<Image> APP_ICON = loadImage(ASSETS_PACKAGE + ICON_FILE_NAME);
	
	public static final Image	PINNED_LIGHT	= loadImage(ASSETS_PACKAGE + ICON_PINNED_LIGHT)
		.get(() -> MISSING_IMAGE_LIGHT);
	public static final Image	PINNED_DARK		= loadImage(ASSETS_PACKAGE + ICON_PINNED_DARK)
		.get(() -> MISSING_IMAGE_DARK);
	public static final Image	UNPINNED_LIGHT	= loadImage(ASSETS_PACKAGE + ICON_UNPINNED_LIGHT)
		.get(() -> MISSING_IMAGE_LIGHT);
	public static final Image	UNPINNED_DARK	= loadImage(ASSETS_PACKAGE + ICON_UNPINNED_DARK)
		.get(() -> MISSING_IMAGE_DARK);
	
	private static Image createMissingImage(boolean light)
	{
		final Color		bgColor			= light ? Color.LIGHT_GRAY : Color.DARK_GRAY;
		final Color		borderColor		= light ? bgColor.darker() : bgColor.brighter();
		final Color		strikeThrough	= light ? Color.RED.brighter() : Color.RED.darker();
		final String[]	buffer			= new String[] {
			"00000000000000000000000000000000",
			"00000000000000000000000000000000",
			"00----------------------------00",
			"00----------------------------00",
			"00----------------------------00",
			"00---88-----------------------00",
			"00---888----------------------00",
			"00----888---------------------00",
			"00-----888--------------------00",
			"00------888-------------------00",
			"00-------888------------------00",
			"00--------888-----------------00",
			"00---------888----------------00",
			"00----------888---------------00",
			"00-----------888--------------00",
			"00------------888-------------00",
			"00-------------888------------00",
			"00--------------888-----------00",
			"00---------------888----------00",
			"00----------------888---------00",
			"00-----------------888--------00",
			"00------------------888-------00",
			"00-------------------888------00",
			"00--------------------888-----00",
			"00---------------------888----00",
			"00----------------------888---00",
			"00-----------------------88---00",
			"00----------------------------00",
			"00----------------------------00",
			"00----------------------------00",
			"00000000000000000000000000000000",
			"00000000000000000000000000000000"
		};
		
		BufferedImage image = new BufferedImage(buffer[0].length(), buffer.length, BufferedImage.TYPE_INT_RGB);
		for (int y : range(image.getHeight()))
			for (int x : range(image.getWidth()))
				image.setRGB(x, y, switch (buffer[y].charAt(x))
				{
				case '0' -> borderColor.getRGB();
				case '-' -> bgColor.getRGB();
				case '8' -> strikeThrough.getRGB();
				default -> Color.PINK.getRGB();
				});
		return image;
	}
}