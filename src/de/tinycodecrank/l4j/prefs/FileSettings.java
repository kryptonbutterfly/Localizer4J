package de.tinycodecrank.l4j.prefs;

import de.tinycodecrank.xmlConfig4J.annotations.Value;

public class FileSettings
{
	@Value
	public String localizationDelimiter = "=";
	
	@Value
	public String langFileExtension = ".lang";
	
	@Deprecated(forRemoval = true, since = "2.0.0")
	@Value
	public boolean usePropertyFiles = true;
	
	@Value
	public FileType languageFileType = FileType.PROPERTIES;
	
	@Value
	public boolean versionListFile = false;
}