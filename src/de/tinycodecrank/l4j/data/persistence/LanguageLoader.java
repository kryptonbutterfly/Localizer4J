package de.tinycodecrank.l4j.data.persistence;

import java.io.File;

public interface LanguageLoader
{
	public void load(Language language, File file);
	
	public void save(Language language, File folder);
	
	public void delete(Language language, File folder);
}