package kryptonbutterfly.l4j.prefs;

import kryptonbutterfly.l4j.util.Constants;
import kryptonbutterfly.xmlConfig4J.annotations.Value;

public class FileSettings
{
	@Value
	public String localizationDelimiter = Constants.LANG_FILE_DEFAULT_DELIM;
	
	@Value
	public String langFileExtension = Constants.LANG_EXTENSION;
	
	@Deprecated(forRemoval = true, since = "2.0.0")
	@Value
	public boolean usePropertyFiles = true;
	
	@Value
	public FileType languageFileType = FileType.PROPERTIES;
	
	@Value
	public boolean versionListFile = false;
}