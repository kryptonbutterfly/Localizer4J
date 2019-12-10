package de.tinycodecrank.l4j.data.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import de.tinycodecrank.l4j.data.gui.Translation;
import de.tinycodecrank.l4j.prefs.FileSettings;
import de.tinycodecrank.l4j.startup.Localizer4J;

public class LangLoader implements LanguageLoader
{
	private FileSettings settings;
	
	public LangLoader(FileSettings settings)
	{
		this.settings = settings;
	}
	
	@Override
	public void load(Language language, File file)
	{
		try (FileInputStream iStream = new FileInputStream(file))
		{
			try (Scanner sc = new Scanner(iStream))
			{
				while (sc.hasNext())
				{
					String line = sc.nextLine().trim();
					if (!line.isEmpty() && !line.startsWith("//"))
					{
						int delimiterIndex = line.indexOf(settings.localizationDelimiter);
						if (delimiterIndex != -1)
						{
							String	key		= line.substring(0, delimiterIndex);
							String	value	= line.substring(
								delimiterIndex + Localizer4J.prefs.fileSettings.localizationDelimiter.length(),
								line.length());
							language.translations.add(new Translation(key.trim(), value.trim()));
						}
					}
				}
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
		File outFile = new File(folder, language.getName() + settings.langFileExtension);
		try (FileWriter fw = new FileWriter(outFile))
		{
			try (BufferedWriter bw = new BufferedWriter(fw))
			{
				final String	delimiter	= " " + settings.localizationDelimiter + " ";
				boolean			firstLine	= true;
				for (Translation translation : language.translations)
				{
					if (firstLine)
					{
						firstLine = false;
					}
					else
					{
						bw.append("\n");
					}
					bw.append(translation.getKey())
						.append(delimiter)
						.append(translation.getTranslation());
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void delete(Language language, File folder)
	{
		File delFile = new File(folder, language.getName() + settings.langFileExtension);
		if (delFile.exists())
		{
			delFile.delete();
		}
	}
}