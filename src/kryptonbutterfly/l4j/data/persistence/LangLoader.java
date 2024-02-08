package kryptonbutterfly.l4j.data.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import kryptonbutterfly.l4j.data.gui.Translation;
import kryptonbutterfly.l4j.misc.Globals;
import kryptonbutterfly.l4j.prefs.FileSettings;

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
								delimiterIndex + Globals.prefs.fileSettings.localizationDelimiter.length(),
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
		File outFile = compute(language, folder);
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
	public String fileExtension()
	{
		return settings.langFileExtension;
	}
}