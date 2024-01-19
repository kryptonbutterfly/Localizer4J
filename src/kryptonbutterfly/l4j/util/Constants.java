package kryptonbutterfly.l4j.util;

import java.awt.Color;
import java.nio.charset.Charset;

public interface Constants
{
	public static final String	UTF8_NAME	= "UTF-8";
	public static final Charset	UTF8		= Charset.forName(UTF8_NAME);
	
	public static final String	JSON_EXTENSION			= ".json";
	public static final String	PROPERTIES_EXTENSION	= ".properties";
	public static final String	LANG_EXTENSION			= ".lang";
	public static final String	JAVA_SOURCE_EXTENSION	= ".java";
	
	public static final String	PROJECT_FILE_NAME		= "lang.project";
	public static final String	LANG_FILE_DEFAULT_DELIM	= "=";
	
	public static final String CURR_DIR_RELATIVE = "./";
	
	public static final String	PROGRAM_DOCK_NAME	= "Localizer4J";
	public static final String	PROGRAM_INFO		= "Localizer4J\n";
	
	public static final Color	light_Red		= new Color(255, 191, 191);
	public static final Color	light_Gray		= new Color(191, 191, 191);
	public static final Color	light_Green		= new Color(191, 255, 191);
	public static final Color	light_Yellow	= new Color(255, 255, 191);
	
	public static final Color	dark_Red	= new Color(64, 0, 0);
	public static final Color	dark_Gray	= new Color(96, 96, 96);
	public static final Color	dark_Green	= new Color(0, 64, 0);
	public static final Color	dark_Yellow	= new Color(64, 64, 0);
}