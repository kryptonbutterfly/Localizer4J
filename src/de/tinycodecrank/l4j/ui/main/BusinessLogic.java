package de.tinycodecrank.l4j.ui.main;

import java.awt.Cursor;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;

import de.tinycodecrank.i18n.Localizer;
import de.tinycodecrank.l4j.config.ProjectConfig;
import de.tinycodecrank.l4j.data.gui.Translatable;
import de.tinycodecrank.l4j.data.gui.Translatable.TranslationState;
import de.tinycodecrank.l4j.data.gui.Translation;
import de.tinycodecrank.l4j.data.persistence.Language;
import de.tinycodecrank.l4j.data.persistence.LanguageLoader;
import de.tinycodecrank.l4j.startup.Localizer4J;
import de.tinycodecrank.l4j.startup.ProgramArgs;
import de.tinycodecrank.l4j.ui.lang.LangGui;
import de.tinycodecrank.l4j.ui.project.ProjectGui;
import de.tinycodecrank.l4j.ui.settings.Settings;
import de.tinycodecrank.l4j.util.KeyEventListener;
import de.tinycodecrank.l4j.util.KeyEventType;
import de.tinycodecrank.l4j.util.Sneaky;
import de.tinycodecrank.l4j.util.StringUtils;
import de.tinycodecrank.math.utils.limit.LimitInt;
import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.util.swing.BusinessLogicTemplate;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;

class BusinessLogic extends BusinessLogicTemplate<MainGui, Localizer>
{
	private String				projectPath	= "";
	private Opt<ProjectConfig>	project		= Opt.empty();
	
	private final Object		lockCursor		= new Object();
	private final Object		lockProject		= new Object();
	private volatile boolean	loadingProject	= false;
	
	private final Localizer l10n;
	
	BusinessLogic(MainGui gui, Localizer l10n)
	{
		super(gui);
		this.l10n = l10n;
	}
	
	void init(ProgramArgs args)
	{
		if (args.projectFile != null && args.projectFile.length > 0)
		{
			File configFile = new File(args.projectFile[0]);
			if (configFile.exists())
			{
				openProject(configFile);
			}
		}
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(gui ->
		{
			Localizer4J.prefs.mainWindow.height	= gui.getHeight();
			Localizer4J.prefs.mainWindow.width	= gui.getWidth();
			Localizer4J.prefs.mainWindow.posX	= gui.getX();
			Localizer4J.prefs.mainWindow.posY	= gui.getY();
			Localizer4J.prefs.mainWindow.state	= gui.getExtendedState();
			
			Localizer4J.prefs.layout.keyTableWidth		= gui.splitPane.getDividerLocation();
			Localizer4J.prefs.layout.translationHeight	= gui.splitPaneValue.getDividerLocation();
			Localizer4J.prefs.layout.occurenceHeight	= gui.splitOccurences.getDividerLocation();
			
			Localizer4J.prefs.pinnedOnTop = gui.pin.isSelected();
		});
	}
	
	private void setLoadingProject(boolean loading)
	{
		synchronized (lockProject)
		{
			this.loadingProject = loading;
		}
	}
	
	private void doIfNotLoadingProject(Runnable run)
	{
		synchronized (lockCursor)
		{
			if (!this.loadingProject)
			{
				run.run();
			}
		}
	}
	
	void createLanguage(ActionEvent ae)
	{
		gui.if_(
			gui -> project.if_(
				project -> new LangGui(
					gui,
					ModalityType.APPLICATION_MODAL,
					event -> event.getReturnValue()
						.filter(s -> event.success())
						.filter(StringUtils::isNotEmpty)
						.if_(s ->
						{
							LanguageLoader loader = project.fileSettings.languageFileType
								.createLoader(project.fileSettings);
							
							Language newLang = new Language(s, loader);
							newLang.dirty(true);
							if (!project.languages.containsKey(s))
							{
								project.languages.put(s, newLang);
								project.deletedLangList.remove(newLang);
								gui.comboBoxLanguage.addItem(newLang);
								gui.comboBoxFallback.addItem(newLang);
							}
						}),
					l10n)));
	}
	
	void deleteLanguage(ActionEvent ae)
	{
		gui.if_(gui -> project.if_(project ->
		{
			Language currLang = (Language) gui.comboBoxLanguage.getSelectedItem();
			if (currLang != null)
			{
				int langCount = 0;
				synchronized (lockProject)
				{
					project.languages.remove(currLang.getName());
					project.deletedLangList.add(currLang);
					langCount = project.languages.size();
				}
				gui.comboBoxLanguage.removeItem(currLang);
				gui.comboBoxLanguage.setSelectedIndex(-1);
				gui.comboBoxFallback.removeItem(currLang);
				
				disableApply();
				
				gui.tableModel.setLanguage(null);
				gui.tableModel.recalculate();
				gui.txtTranslation.setText("");
				gui.txtTranslationFallback.setText("");
				gui.tableModelClasses.setContent(null, null, this.projectPath);
				gui.tableModelMisc.setContent(null, null, this.projectPath);
				
				if (langCount <= 0)
				{
					gui.mntmDelete.setEnabled(false);
					gui.mntmImport.setEnabled(false);
					gui.btnAddKey.setEnabled(false);
					gui.txtAddKey.setEnabled(false);
					gui.btnRemove.setEnabled(false);
				}
			}
		}));
	}
	
	void importLanguage(ActionEvent ae)
	{
		gui.if_(gui ->
		{
			project.if_(project ->
			{
				final String	extension	= project.fileSettings.languageFileType.extension(project.fileSettings);
				final File		langFolder	= new File(project.langFolder);
				JFileChooser	chooser		= new JFileChooser(project.langFolder);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setMultiSelectionEnabled(true);
				
				FileFilter filter = new FileFilter()
				{
					@Override
					public String getDescription()
					{
						return extension;
					}
					
					@Override
					public boolean accept(File f)
					{
						return Sneaky.sneaky(() ->
						{
							final String	fileFolder		= f.getParentFile().getCanonicalPath();
							final String	langDirectory	= langFolder.getCanonicalPath();
							return f.getPath().endsWith(extension) && fileFolder.equals(langDirectory);
						});
					}
				};
				chooser.setFileFilter(filter);
				int resultCode = chooser.showDialog(gui, l10n.localize("FileBrowser.open"));
				if (resultCode == JFileChooser.APPROVE_OPTION)
					importFiles(gui, project, chooser.getSelectedFiles());
			});
		});
	}
	
	void showProjectOptions(ActionEvent ae)
	{
		gui.if_(gui -> project.if_(project -> new Settings(gui, ModalityType.APPLICATION_MODAL, event ->
		{}, project.fileSettings, l10n)));
	}
	
	void showGeneralOptions(ActionEvent ae)
	{
		gui.if_(gui -> new Settings(gui, ModalityType.APPLICATION_MODAL, event ->
		{}, Localizer4J.prefs.fileSettings, l10n));
	}
	
	KeyListener changeTranslation()
	{
		return KeyEventListener.create(
			KeyEventType.PRESSED,
			KeyEvent.CTRL_DOWN_MASK,
			e ->
			{
				changeTranslation(null);
				return true;
			},
			KeyEvent.VK_ENTER);
	}
	
	void changeTranslation(ActionEvent ae)
	{
		gui.if_(gui -> project.if_(project ->
		{
			int selection = gui.table.getSelectedRow();
			if (selection < 0)
				return;
			
			Language		lang			= (Language) gui.comboBoxLanguage.getSelectedItem();
			Translatable	translatable	= gui.tableModel.getTranslatable(selection);
			if (translatable instanceof Translation)
			{
				String		newValue	= gui.txtTranslation.getText();
				Translation	translation	= (Translation) translatable;
				if (!newValue.equals(translation.getTranslation()))
				{
					translation.setTranslation(gui.txtTranslation.getText());
					lang.dirty(true);
				}
			}
			else
			{
				Translation translation = new Translation(translatable.getKey(), gui.txtTranslation.getText());
				lang.dirty(true);
				lang.translations.add(translation);
				if (project.sources.containsKey(translatable.getKey()))
				{
					translation.setTranslationState(TranslationState.TRANSLATED);
				}
				else
				{
					translation.setTranslationState(TranslationState.TRANSLATED_UNUSED);
				}
				gui.tableModel.recalculate();
			}
		}));
	}
	
	void tableSelectionListener(ListSelectionEvent event)
	{
		gui.if_(gui -> project.if_(project ->
		{
			gui.txtTranslation.setText("");
			gui.txtTranslationFallback.setText("");
			gui.tableModelClasses.clearContent();
			gui.tableModelMisc.clearContent();
			gui.btnRemove.setEnabled(false);
			gui.btnApply.setEnabled(false);
			
			int	selection	= gui.table.getSelectedRow();
			int	tableSize	= gui.tableModel.getRowCount();
			if (LimitInt.inRange(0, selection, tableSize - 1))
			{
				String target = gui.tableModel.getStringAt(selection, 1);
				if (target != null)
				{
					Language currentLang = (Language) gui.comboBoxLanguage.getSelectedItem();
					if (currentLang != null)
					{
						for (Translation translation : currentLang.translations)
						{
							if (translation.getKey().equals(target))
							{
								gui.txtTranslation.setText(translation.getTranslation());
								break;
							}
						}
					}
					Language fallbackLang = (Language) gui.comboBoxFallback.getSelectedItem();
					if (fallbackLang != null)
					{
						for (Translation translation : fallbackLang.translations)
						{
							if (translation.getKey().equals(target))
							{
								gui.txtTranslationFallback.setText(translation.getTranslation());
							}
						}
					}
					gui.tableModelClasses.setContent(project.sources, target, projectPath);
					gui.tableModelMisc.setContent(project.misc, target, projectPath);
					gui.btnRemove.setEnabled(true);
				}
			}
		}));
	}
	
	void selectLanguage(ActionEvent ae)
	{
		gui.if_(gui -> project.if_(project ->
		{
			Opt<Translatable> selectedTranslation = Opt.of(gui.tableModel.getTranslatable(gui.table.getSelectedRow()));
			
			Language selected = (Language) gui.comboBoxLanguage.getSelectedItem();
			gui.table.getSelectionModel().setSelectionInterval(0, -1);
			gui.txtTranslation.setText("");
			gui.txtTranslationFallback.setText("");
			gui.btnApply.setEnabled(false);
			gui.btnRemove.setEnabled(false);
			if (selected != null
					&& project.selectedLanguage != null
					&& !project.selectedLanguage.equals(selected.getName()))
			{
				project.selectedLanguage = selected.getName();
			}
			else
			{
				project.selectedLanguage = null;
			}
			
			if (selected != null)
			{
				gui.btnAddKey.setEnabled(true);
				gui.txtAddKey.setEnabled(true);
			}
			
			gui.tableModel.setLanguage(selected);
			gui.tableModel.recalculate();
			selectedTranslation.if_(t -> setSelection(t.getKey()));
		}));
	}
	
	void selectFallback(ActionEvent ae)
	{
		gui.if_(gui -> project.if_(project ->
		{
			Language selected = (Language) gui.comboBoxFallback.getSelectedItem();
			gui.txtTranslationFallback.setText("");
			if (selected != null)
			{
				project.fallback = selected.getName();
				
				int row = gui.table.getSelectedRow();
				if (row >= 0)
				{
					Translatable translatable = gui.tableModel.getTranslatable(row);
					if (translatable instanceof Translation)
					{
						for (Translation t : selected.translations)
						{
							if (t.getKey().equals(translatable.getKey()))
							{
								gui.txtTranslationFallback.setText(t.getTranslation());
								break;
							}
						}
					}
				}
			}
			else
			{
				project.fallback = null;
			}
		}));
	}
	
	void removeButton(ActionEvent ae)
	{
		gui.if_(gui ->
		{
			int	selection	= gui.table.getSelectedRow();
			int	tableSize	= gui.tableModel.getRowCount();
			if (LimitInt.inRange(0, selection, tableSize - 1))
			{
				String target = gui.tableModel.getStringAt(selection, 1);
				gui.tableModel.removeRow(selection);
				remove(target);
				((Language) gui.comboBoxLanguage.getSelectedItem()).dirty(true);
				tableSize--;
				if (selection < tableSize)
				{
					gui.table.setRowSelectionInterval(selection, selection);
				}
				else if (selection > 0)
				{
					selection--;
					gui.table.setRowSelectionInterval(selection, selection);
				}
			}
		});
	}
	
	private void remove(String key)
	{
		gui.if_(gui -> project.if_(project ->
		{
			synchronized (lockProject)
			{
				gui.tableModel.removeRow(key);
			}
			disableApply();
		}));
	}
	
	private void disableApply()
	{
		gui.if_(gui ->
		{
			gui.btnApply.setEnabled(false);
		});
	}
	
	void changePinned(ActionEvent ae)
	{
		gui.if_(gui -> gui.setAlwaysOnTop(gui.pin.isSelected()));
	}
	
	void updateRecent()
	{
		gui.if_(gui ->
		{
			gui.mnOpenRecent.removeAll();
			Localizer4J.prefs.history.recent.forEach(location ->
			{
				File		file	= new File(location);
				JMenuItem	item	= new JMenuItem(location);
				item.addActionListener(ae -> openProject(file));
				gui.mnOpenRecent.add(item);
			});
		});
	}
	
	private void addRecent(File file)
	{
		gui.if_(gui ->
		{
			Localizer4J.prefs.history.recent.remove(file.getPath());
			Localizer4J.prefs.history.recent.addFirst(file.getPath());
			while (Localizer4J.prefs.history.recent.size() > Localizer4J.prefs.history.maxLength)
			{
				Localizer4J.prefs.history.recent.removeLast();
			}
			updateRecent();
		});
	}
	
	void newProject(ActionEvent ae)
	{
		gui.if_(
			gui -> EventQueue
				.invokeLater(() -> new ProjectGui(gui, ModalityType.APPLICATION_MODAL, this::createNewProject, l10n)));
	}
	
	private void createNewProject(GuiCloseEvent<ProjectConfig> event)
	{
		gui.filter(gui -> event.success()).if_(gui ->
		{
			event.getReturnValue().if_(project ->
			{
				this.project = Opt.of(project);
				
				gui.comboBoxLanguage.removeAllItems();
				gui.comboBoxFallback.removeAllItems();
				
				gui.comboBoxLanguage.setEnabled(true);
				gui.comboBoxFallback.setEnabled(true);
				gui.mnLanguage.setEnabled(true);
				
				gui.tableModelClasses.clearContent();
				gui.tableModelMisc.clearContent();
				gui.tableModel.setLanguage(null);
				gui.tableModel.setContent(project.sources, project.misc, project.languages);
				
				gui.btnAddKey.setEnabled(false);
				gui.txtAddKey.setEnabled(false);
				
				gui.mntmSaveProject.setEnabled(true);
				
				gui.txtTranslation.setText("");
				gui.txtTranslationFallback.setText("");
				
				gui.mntmProjectSettings.setEnabled(true);
			});
		});
	}
	
	void openProject(ActionEvent ae)
	{
		gui.if_(gui ->
		{
			JFileChooser chooser = new JFileChooser();
			
			FileFilter filter = new FileFilter()
			{
				@Override
				public String getDescription()
				{
					return ProjectConfig.PROJECT_FILE_NAME;
				}
				
				@Override
				public boolean accept(File f)
				{
					return f.getPath().endsWith(ProjectConfig.PROJECT_FILE_NAME)
							|| f.isDirectory();
				}
			};
			chooser.setFileFilter(filter);
			int resultCode = chooser.showDialog(gui, l10n.localize("FileBrowser.open"));
			if (resultCode == JFileChooser.APPROVE_OPTION)
			{
				openProject(chooser.getSelectedFile());
			}
		});
	}
	
	private void importFiles(MainGui gui, ProjectConfig project, File[] files)
	{
		System.out.println("importing " + files.length + " files.");
		Arrays.stream(files)
			.filter(f -> !project.languages.containsKey(f.getName()))
			.forEach(f ->
			{
				new Thread(() ->
				{
					final var selected	= (Language) gui.comboBoxLanguage.getSelectedItem();
					final var fallback	= (Language) gui.comboBoxFallback.getSelectedItem();
					
					final var langName		= f.getName().substring(0, f.getName().indexOf('.'));
					final var fileSettings	= project.fileSettings;
					final var loader		= fileSettings.languageFileType.createLoader(fileSettings);
					final var extension		= project.fileSettings.languageFileType.extension(project.fileSettings);
					
					final var language = project.loadLanguage(langName, extension, loader);
					
					gui.comboBoxLanguage.addItem(language);
					gui.comboBoxFallback.addItem(language);
					
					gui.comboBoxLanguage.setSelectedItem(selected);
					gui.comboBoxFallback.setSelectedItem(fallback);
				}).start();
			});
	}
	
	void openProject(File projectFile)
	{
		gui.if_(gui ->
		{
			Opt<ProjectConfig> loadingProject = Opt.of(new ProjectConfig(projectFile));
			
			gui.comboBoxLanguage.removeAllItems();
			gui.comboBoxFallback.removeAllItems();
			
			new Thread(() ->
			{
				loadingProject.if_(project ->
				{
					this.setLoadingProject(true);
					gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					try
					{
						project.load();
						project.loadLanguages();
						project.loadSources();
						
						project.languages.values().forEach(lang ->
						{
							gui.comboBoxLanguage.addItem(lang);
							gui.comboBoxFallback.addItem(lang);
						});
						gui.comboBoxLanguage.setEnabled(true);
						gui.comboBoxFallback.setEnabled(true);
						gui.mnLanguage.setEnabled(true);
						gui.mntmDelete.setEnabled(true);
						gui.mntmImport.setEnabled(true);
						
						gui.comboBoxFallback.setSelectedItem(
							Opt.of(project.fallback)
								.map(project.languages::get)
								.get(() -> null));
						
						Language selectedLang = project.languages.get(project.selectedLanguage);
						gui.txtAddKey.setEnabled(true);
						gui.btnAddKey.setEnabled(true);
						gui.mntmSaveProject.setEnabled(true);
						
						gui.comboBoxLanguage.setSelectedItem(selectedLang);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					
					this.setLoadingProject(false);
					this.doIfNotLoadingProject(() -> gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)));
					
					synchronized (lockProject)
					{
						gui.tableModel.setLanguage((Language) gui.comboBoxLanguage.getSelectedItem());
						gui.tableModel.setContent(project.sources, project.misc, project.languages);
					}
					gui.mntmProjectSettings.setEnabled(true);
					addRecent(project.getProjectFile());
				});
				this.project = loadingProject;
			}).start();
		});
	}
	
	void saveProject(ActionEvent ae)
	{
		gui.if_(gui -> project.if_(project ->
		{
			Language fallbackLang = (Language) gui.comboBoxFallback.getSelectedItem();
			if (fallbackLang != null)
			{
				project.fallback = fallbackLang.getName();
			}
			else
			{
				project.fallback = null;
			}
			Language selectedLang = (Language) gui.comboBoxLanguage.getSelectedItem();
			if (selectedLang != null)
			{
				project.selectedLanguage = selectedLang.getName();
			}
			else
			{
				project.selectedLanguage = null;
			}
			
			project.saveLanguages();
			project.save();
			
			addRecent(project.getProjectFile());
		}));
	}
	
	KeyListener addKey()
	{
		return KeyEventListener.create(
			KeyEventType.PRESSED,
			e ->
			{
				addKey(null);
				return true;
			},
			KeyEvent.VK_ENTER,
			KeyEvent.VK_ACCEPT);
	}
	
	void addKey(ActionEvent ae)
	{
		gui.if_(gui -> project.if_(project ->
		{
			String key = gui.txtAddKey.getText();
			gui.txtAddKey.setText("");
			if (StringUtils.isNotBlank(key))
			{
				Language lang = (Language) gui.comboBoxLanguage.getSelectedItem();
				if (lang != null)
				{
					Translation translatable = new Translation(key);
					lang.translations.add(translatable);
					lang.dirty(true);
					gui.tableModel.recalculate();
					setSelection(key);
				}
			}
		}));
	}
	
	private void setSelection(String selection)
	{
		gui.if_(gui ->
		{
			final int target = gui.tableModel.find(selection);
			if (target == -1)
				return;
			gui.table.setRowSelectionInterval(target, target);
			final var	viewport	= (JViewport) gui.table.getParent();
			final var	rect		= gui.table.getCellRect(target, 1, true);
			final var	offset		= viewport.getViewPosition();
			final int	x			= rect.x - offset.x;
			final int	y			= rect.y - offset.y;
			rect.setLocation(x, y);
			gui.table.scrollRectToVisible(rect);
		});
	}
}