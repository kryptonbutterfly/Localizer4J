package de.tinycodecrank.l4j.data.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import de.tinycodecrank.l4j.data.gui.Translation;
import de.tinycodecrank.l4j.util.Constants;

public class PropertiesLoader implements LanguageLoader
{
	@Override
	public void load(Language language, File file)
	{
		try (FileInputStream iStream = new FileInputStream(file))
		{
			Properties props = new Properties();
			props.load(iStream);
			props.forEach((key, value) -> language.translations.add(new Translation((String) key, (String) value)));
			// for (Entry<Object, Object> entry : props.entrySet())
			// language.translations.add(new Translation((String) entry.getKey(), (String)
			// entry.getValue()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void save(Language language, File folder)
	{
		try (FileOutputStream oStream = new FileOutputStream(compute(language, folder), false))
		{
			Properties props = new Properties();
			for (Translation entry : language.translations)
				props.setProperty(entry.getKey(), entry.getTranslation());
			
			props.store(oStream, null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public String fileExtension()
	{
		return Constants.PROPERTIES_EXTENSION;
	}
}