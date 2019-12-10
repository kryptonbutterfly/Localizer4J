package de.tinycodecrank.l4j.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.tinycodecrank.collections.data.Tuple;
import de.tinycodecrank.i18n.config.LanguageVersionConfig;
import de.tinycodecrank.l4j.data.index.ProjectStringsIndex;
import de.tinycodecrank.l4j.data.persistence.LangLoader;
import de.tinycodecrank.l4j.data.persistence.Language;
import de.tinycodecrank.l4j.data.persistence.LanguageLoader;
import de.tinycodecrank.l4j.data.persistence.ProjectReader;
import de.tinycodecrank.l4j.data.persistence.PropertiesLoader;
import de.tinycodecrank.l4j.prefs.FileSettings;
import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.xmlConfig4J.FileConfig;
import de.tinycodecrank.xmlConfig4J.annotations.Value;

public class ProjectConfig extends FileConfig
{
	public static final String	PROPERTIES_EXT		= ".properties";
	public static final String	PROJECT_FILE_NAME	= "lang.project";
	
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
		String			extension;
		LanguageLoader	loader;
		if (fileSettings.usePropertyFiles)
		{
			extension	= PROPERTIES_EXT;
			loader		= new PropertiesLoader();
		}
		else
		{
			extension	= fileSettings.langFileExtension;
			loader		= new LangLoader(fileSettings);
		}
		
		languageNames.stream()
			.map(name -> new Tuple<>(name, new File(langFolder(), name + extension)))
			.map(tuple -> new Tuple<>(tuple.first(), new Language(tuple.second(), loader)))
			.forEach(t -> languages.put(t.first(), t.second()));
		
		if (this.fileSettings.versionListFile)
		{
			LanguageVersionConfig	langVersionConfig	= new LanguageVersionConfig();
			File					langVersionFile		= new File(langFolder(), LanguageVersionConfig.FILE_NAME);
			if (langVersionFile.exists())
			{
				try (FileInputStream iStream = new FileInputStream(langVersionFile))
				{
					langVersionConfig.load(iStream);
					langVersionConfig.languages.forEach(
						(name, version) -> Opt.of(languages.get(name))
							.if_(l -> l.version = version));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void saveLanguages()
	{
		HashSet<String>			set					= new HashSet<>();
		LanguageVersionConfig	langVersionConfig	= new LanguageVersionConfig();
		languages.entrySet().forEach(e ->
		{
			set.add(e.getKey());
			Language lang = e.getValue();
			lang.save(langFolder());
			
			langVersionConfig.languages.put(lang.getName(), Integer.valueOf(lang.version));
		});
		deletedLangList.forEach(l -> l.delete(langFolder()));
		languageNames = set;
		
		if (this.fileSettings.versionListFile)
		{
			File langVersionFile = new File(langFolder(), LanguageVersionConfig.FILE_NAME);
			try (FileOutputStream oStream = new FileOutputStream(langVersionFile))
			{
				langVersionConfig.save(oStream);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void loadSources()
	{
		sourceFolder().if_(sourceFolder ->
		{
			ArrayList<File>	sourceFiles	= ProjectReader.findSourceFiles(sourceFolder);
			ArrayList<File>	miscFiles	= ProjectReader.findMiscFiles(sourceFolder, this);
			
			int multiplier = 1;
			if (sourceFiles.size() > 0 && sourceFiles.size() < miscFiles.size())
			{
				multiplier = miscFiles.size() / sourceFiles.size();
			}
			ExecutorService	miscExecutor	= Executors
				.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * multiplier);
			ExecutorService	sourceExecutor	= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			
			ProjectReader.loadMisc(miscFiles, sourceFolder.toString(), miscExecutor, misc);
			ProjectReader.scan(sourceFiles, sourceFolder.toString(), sourceExecutor, sources);
			
			sourceExecutor.shutdown();
			miscExecutor.shutdown();
			try
			{
				sourceExecutor.awaitTermination(600, TimeUnit.SECONDS);
				miscExecutor.awaitTermination(600, TimeUnit.SECONDS);
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