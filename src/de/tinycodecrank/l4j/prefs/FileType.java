package de.tinycodecrank.l4j.prefs;

import java.util.Arrays;
import java.util.function.Function;

import de.tinycodecrank.l4j.data.persistence.JsonLoader;
import de.tinycodecrank.l4j.data.persistence.LangLoader;
import de.tinycodecrank.l4j.data.persistence.LanguageLoader;
import de.tinycodecrank.l4j.data.persistence.PropertiesLoader;
import de.tinycodecrank.l4j.util.Constants;
import de.tinycodecrank.l4j.util.LangManager;
import de.tinycodecrank.monads.opt.Opt;

public enum FileType implements Constants
{
	PROPERTIES("FileType.Properties", s -> new PropertiesLoader(), PROPERTIES_EXTENSION),
	LANG("FileType.Lang", s -> new LangLoader(s)),
	JSON("FileType.Json", s -> new JsonLoader(), JSON_EXTENSION);
	
	private final Function<FileSettings, LanguageLoader>	loader;
	private final Opt<String>								extension;
	private final String									name;
	
	private FileType(String name, Function<FileSettings, LanguageLoader> loader)
	{
		this.loader		= loader;
		this.extension	= Opt.empty();
		this.name		= name;
	}
	
	private FileType(String name, Function<FileSettings, LanguageLoader> loader, String extension)
	{
		this.loader		= loader;
		this.extension	= Opt.of(extension);
		this.name		= name;
	}
	
	public LanguageLoader createLoader(FileSettings settings)
	{
		return loader.apply(settings);
	}
	
	public String extension(FileSettings settings)
	{
		return extension.get(() -> settings.langFileExtension);
	}
	
	public boolean varExtension()
	{
		return !extension.isPresent();
	}
	
	public static LocalizingFileType match(FileType type, LocalizingFileType[] types)
	{
		for (var t : types)
			if (t.type() == type)
				return t;
		return null;
	}
	
	public static LocalizingFileType[] localized(LangManager l10n)
	{
		return Arrays.stream(values())
			.map(l(l10n))
			.toArray(LocalizingFileType[]::new);
	}
	
	private static Function<FileType, LocalizingFileType> l(LangManager l10n)
	{
		return type ->
		{
			var localization = l10n.get(type.name, s ->
			{});
			return new LocalizingFileType()
			{
				@Override
				public FileType type()
				{
					return type;
				}
				
				@Override
				public String toString()
				{
					return localization.get();
				}
			};
		};
	}
	
	public static interface LocalizingFileType
	{
		public String toString();
		
		public FileType type();
	}
}