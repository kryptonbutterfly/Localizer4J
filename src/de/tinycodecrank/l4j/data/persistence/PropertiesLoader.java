package de.tinycodecrank.l4j.data.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import de.tinycodecrank.l4j.data.gui.Translation;

public class PropertiesLoader implements LanguageLoader
{
	@Override
	public void load(Language language, File file)
	{
		try (FileInputStream iStream = new FileInputStream(file))
		{
			Properties props = new Properties();
			props.load(iStream);
			for (Entry<Object, Object> entry : props.entrySet())
			{
				language.translations.add(new Translation((String) entry.getKey(), (String) entry.getValue()));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void save(Language language, File folder)
	{
		String lang = language.getName() + ".properties";
		try (FileOutputStream oStream = new FileOutputStream(new File(folder, lang), false))
		{
			Properties props = new Properties();
			for (Translation entry : language.translations)
			{
				props.setProperty(entry.getKey(), entry.getTranslation());
			}
			props.store(oStream, null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void delete(Language language, File folder)
	{
		File delFile = new File(folder, language.getName());
		if (delFile.exists())
		{
			delFile.delete();
		}
	}
}