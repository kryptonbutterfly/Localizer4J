package de.tinycodecrank.l4j.util;

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
}