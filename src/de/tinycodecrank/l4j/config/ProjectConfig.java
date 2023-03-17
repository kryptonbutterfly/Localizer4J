package de.tinycodecrank.l4j.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.tinycodecrank.i18n.config.LanguageVersionConfig;
import de.tinycodecrank.l4j.data.index.ProjectStringsIndex;
import de.tinycodecrank.l4j.data.persistence.Language;
import de.tinycodecrank.l4j.data.persistence.LanguageLoader;
import de.tinycodecrank.l4j.data.persistence.ProjectReader;
import de.tinycodecrank.l4j.prefs.FileSettings;
import de.tinycodecrank.l4j.util.Constants;
import de.tinycodecrank.math.utils.limit.LimitInt;
import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.xmlConfig4J.FileConfig;
import de.tinycodecrank.xmlConfig4J.annotations.Value;

public class ProjectConfig extends FileConfig implements Constants
{
	private static final int loadTimeOutMS = 6000;
	
	public ProjectConfig(File config)
	{
		super(config);
	}
	
	@Value
	public String sourceFolder = null;
	
	@Value
	public String langFolder = null;
	
	@Value
	public HashSet<String> languageNames = new HashSet<>();
	
	@Value
	public String fallback = null;
	
	@Value
	public String selectedLanguage = null;
	
	@Value
	public FileSettings fileSettings = new FileSettings();
	
	public HashMap<String, Language>	languages		= new HashMap<>();
	public HashSet<Language>			deletedLangList	= new HashSet<>();
	
	public ProjectStringsIndex misc = new ProjectStringsIndex();
	
	public ProjectStringsIndex sources = new ProjectStringsIndex();
	
	private File langFolder()
	{
		return new File(configFile.getParentFile(), langFolder);
	}
	
	private Opt<File> sourceFolder()
	{
		if (sourceFolder != null)
		{
			return Opt.of(new File(configFile.getParentFile(), sourceFolder));
		}
		else
		{
			return Opt.empty();
		}
	}
	
	public void loadLanguages()
	{
		final var		fileType	= fileSettings.languageFileType;
		String			extension	= fileType.extension(fileSettings);
		LanguageLoader	loader		= fileType.createLoader(fileSettings);
		
		languageNames.stream().forEach(name ->
		{
			final var	file		= new File(langFolder(), name + extension);
			final var	language	= new Language(file, loader);
			languages.put(name, language);
		});
		
		if (this.fileSettings.versionListFile)
			loadVersionFile();
	}
	
	private void loadVersionFile()
	{
		final var langVersionFile = new File(langFolder(), LanguageVersionConfig.FILE_NAME);
		if (!langVersionFile.exists())
			return;
		
		final var langVersionConfig = new LanguageVersionConfig();
		
		try (final var iStream = new FileInputStream(langVersionFile))
		{
			langVersionConfig.load(iStream);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		langVersionConfig.languages.forEach((name, version) ->
		{
			Opt.of(languages.get(name))
				.if_(l -> l.version = version);
		});
	}
	
	public void saveLanguages()
	{
		HashSet<String>			set					= new HashSet<>();
		LanguageVersionConfig	langVersionConfig	= new LanguageVersionConfig();
		languages.forEach((name, lang) ->
		{
			set.add(name);
			lang.save(langFolder());
			
			langVersionConfig.languages.put(lang.getName(), Integer.valueOf(lang.version));
		});
		deletedLangList.forEach(l -> l.delete(langFolder()));
		languageNames = set;
		
		if (this.fileSettings.versionListFile)
			saveVersionFile(langVersionConfig);
	}
	
	private void saveVersionFile(LanguageVersionConfig langVersionConfig)
	{
		final var langVersionFile = new File(langFolder(), LanguageVersionConfig.FILE_NAME);
		try (FileOutputStream oStream = new FileOutputStream(langVersionFile))
		{
			langVersionConfig.save(oStream);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadSources()
	{
		sourceFolder().if_(sourceFolder ->
		{
			final ArrayList<File>	sourceFiles	= ProjectReader.findSourceFiles(sourceFolder);
			final ArrayList<File>	miscFiles	= ProjectReader.findMiscFiles(sourceFolder, this.fileSettings);
			
			int multiplier = 1;
			if (LimitInt.inRange(1, sourceFiles.size(), miscFiles.size() - 1))
				multiplier = miscFiles.size() / sourceFiles.size();
			
			final var	miscExecutor	= Executors
				.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * multiplier);
			final var	sourceExecutor	= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			
			ProjectReader.loadMisc(miscFiles, sourceFolder.toString(), miscExecutor, misc);
			ProjectReader.scan(sourceFiles, sourceFolder.toString(), sourceExecutor, sources);
			
			sourceExecutor.shutdown();
			miscExecutor.shutdown();
			try
			{
				sourceExecutor.awaitTermination(loadTimeOutMS, TimeUnit.MILLISECONDS);
				miscExecutor.awaitTermination(loadTimeOutMS, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			sourceExecutor.shutdownNow();
			miscExecutor.shutdownNow();
		});
	}
	
	public File getProjectFile()
	{
		return this.configFile;
	}
}