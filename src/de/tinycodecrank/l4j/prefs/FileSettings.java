package de.tinycodecrank.l4j.prefs;

import de.tinycodecrank.xmlConfig4J.annotations.Value;

public class FileSettings
{
	@Value
	public String localizationDelimiter = "=";
	
	@Value
	public String langFileExtension = ".lang";
	
	@Value
	public boolean usePropertyFiles = true;
	
	@Value
	public boolean versionListFile = false;
}