package kryptonbutterfly.l4j.startup;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import kryptonbutterfly.i18n.I18nStreamConfig;
import kryptonbutterfly.i18n.LocalizationManager;
import kryptonbutterfly.i18n.Localizer;
import kryptonbutterfly.i18n.MissingTranslationException;
import kryptonbutterfly.i18n.ResourceLoader;
import kryptonbutterfly.io.FileSystemUtils;
import kryptonbutterfly.l4j.misc.Assets;
import kryptonbutterfly.l4j.misc.Globals;
import kryptonbutterfly.l4j.prefs.Prefs;
import kryptonbutterfly.l4j.ui.main.MainGui;
import kryptonbutterfly.l4j.util.Constants;
import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.reflectionUtils.Accessor;
import kryptonbutterfly.shortcut.FreedesktopOrgShortcut;
import kryptonbutterfly.util.io.stream.StreamUtils;
import kryptonbutterfly.util.swing.ObservableGui;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.xmlConfig4J.FileConfig;

public class Localizer4J
{
	// private static final File APP_FOLDER = new File(
	// Platforms.getAppDataFile(),
	// File.separator + ".config" + File.separator + "Localizer4J");
	// private static final File CONFIG_FOLDER = new File(APP_FOLDER, "configs");
	// private static final File LOG_FOLDER = new File(APP_FOLDER, "logs");
	// private static final File LOG_FILE = new File(LOG_FOLDER, "localizer4J.log");
	
	static
	{
		System.setProperty("localizer4J.logfile", Globals.LOG_FILE.getPath());
	}
	// public static final Prefs prefs = new Prefs(new File(CONFIG_FOLDER,
	// "config.xml"));
	
	public static void main(String[] args)
	{
		main(new ProgramArgs(args, Constants.PROGRAM_INFO));
	}
	
	private static void main(ProgramArgs args)
	{
		loadConfig(Globals.prefs);
		loadConfig(Globals.windowStates);
		try
		{
			Localizer l10n = loadI18n(Globals.prefs);
			
			setLookAndFeel();
			
			ObservableGui.setDefaultAppImage(Assets.APP_ICON);
			
			if (!args.initialized && args.projectFile == null)
				createAppShortcut();
			
			EventQueue.invokeLater(() -> new MainGui(Localizer4J::persistConfigs, args, l10n));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void persistConfigs(GuiCloseEvent<Void> gce)
	{
		Globals.prefs.save();
		Globals.windowStates.save();
	}
	
	private static Localizer loadI18n(Prefs prefs) throws IOException
	{
		final String			localizationConfig	= Assets.ASSETS_LANGS + "i18n.xml";
		final I18nStreamConfig	i18nConfig			= new I18nStreamConfig();
		final ResourceLoader	resourceLoader		= Localizer4J.class::getResourceAsStream;
		
		LocalizationManager.loadConfig(i18nConfig, localizationConfig, resourceLoader);
		
		LocalizationManager localizationManager = new LocalizationManager(i18nConfig, resourceLoader);
		localizationManager.addLocalizationListener(event -> {
			final String message = "No translation found for:\"%s\" in %s"
				.formatted(event.identifier(), event.language());
			if (localizationManager.throwWhenMissing)
			{
				event.exception = new MissingTranslationException(message);
			}
			else
			{
				System.err.println(message);
				event.returnValue = event.identifier();
			}
			event.consume();
		});
		Localizer localizer = Opt.of(prefs.language)
			.map(localizationManager::buildLocalizer)
			.get(localizationManager::buildLocalizer);
		if (prefs.language == null)
			prefs.language = localizer.currentLanguage();
		return localizer;
	}
	
	private static void loadConfig(FileConfig config)
	{
		if (config.exists())
		{
			try
			{
				config.load();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				config.save();
			}
		}
		else
		{
			config.save();
		}
	}
	
	private static void setLookAndFeel()
	{
		try
		{
			final Toolkit toolkit = Toolkit.getDefaultToolkit();
			new Accessor<>(toolkit, toolkit.getClass().getDeclaredField("awtAppClassName"))
				.applyObj(Field::set, Constants.PROGRAM_DOCK_NAME);
		}
		catch (
			InaccessibleObjectException
			| IllegalArgumentException
			| IllegalAccessException
			| NoSuchFieldException
			| SecurityException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (
			ClassNotFoundException
			| InstantiationException
			| IllegalAccessException
			| UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void createAppShortcut()
	{
		try (InputStream iStream = Assets.class.getResourceAsStream(Assets.ASSETS_PACKAGE + Assets.SVG_ICON_FILE))
		{
			try (FileOutputStream oStream = new FileOutputStream(new File(Assets.SVG_ICON_FILE)))
			{
				StreamUtils.pipe(iStream, oStream);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		FreedesktopOrgShortcut shortcut = new FreedesktopOrgShortcut(new File(Assets.APPLICATION_NAME + ".desktop"));
		FileSystemUtils.getProgramMainJar().if_(jarFile -> {
			shortcut.exec			= Opt.of("java -jar " + jarFile.getAbsolutePath() + " -i -pF ");
			shortcut.type			= Opt.of("Application");
			shortcut.terminal		= Opt.of("false");
			shortcut.version		= Opt.of("1.0");
			shortcut.icon			= Opt.of(new File(Assets.ICON_FILE_NAME).getAbsolutePath());
			shortcut.name			= Opt.of(new FreedesktopOrgShortcut.LocalizableArgument(Assets.APPLICATION_NAME));
			shortcut.categories		= Opt.of("Development;");
			shortcut.genericName	= Opt.of(new FreedesktopOrgShortcut.LocalizableArgument(Assets.APPLICATION_NAME));
			shortcut.keywords		= Opt.of("i18n, l10n, translate, localize");
			try
			{
				shortcut.createShortcut();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}).else_(() -> System.out.println("running in IDE? - skipping shortcut creation"));
	}
}