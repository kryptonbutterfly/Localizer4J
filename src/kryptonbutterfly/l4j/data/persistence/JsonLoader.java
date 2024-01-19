package kryptonbutterfly.l4j.data.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kryptonbutterfly.l4j.data.gui.Translation;
import kryptonbutterfly.l4j.util.Constants;

public class JsonLoader implements LanguageLoader, Constants
{
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public void load(Language language, File file)
	{
		try
		{
			final var json = Files.readString(file.toPath(), UTF8);
			gson.<HashMap<String, String>>fromJson(json, HashMap.class)
				.forEach((key, value) -> language.translations.add(new Translation(key, value)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void save(Language language, File folder)
	{
		final var	file	= compute(language, folder);
		final var	mapping	= new HashMap<String, String>();
		
		for (Translation entry : language.translations)
			mapping.put(entry.getKey(), entry.getTranslation());
		
		try
		{
			Files.writeString(
				file.toPath(),
				gson.toJson(mapping),
				UTF8,
				StandardOpenOption.WRITE,
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public String fileExtension()
	{
		return JSON_EXTENSION;
	}
}