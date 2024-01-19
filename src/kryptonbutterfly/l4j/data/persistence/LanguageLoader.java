package kryptonbutterfly.l4j.data.persistence;

import java.io.File;

public interface LanguageLoader
{
	public void load(Language language, File file);
	
	public void save(Language language, File folder);
	
	public default void delete(Language language, File folder)
	{
		final var delFile = compute(language, folder);
		if (delFile.exists() && !delFile.delete())
			System.out.println("Deleting file: '%s' failed!".formatted(delFile));
	}
	
	public String fileExtension();
	
	public default File compute(Language language, File folder)
	{
		return new File(folder, language.getName() + fileExtension());
	}
}