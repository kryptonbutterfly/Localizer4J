package de.tinycodecrank.l4j.ui.project;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;

import javax.swing.JFileChooser;

import de.tinycodecrank.l4j.config.ProjectConfig;
import de.tinycodecrank.l4j.startup.Localizer4J;
import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.util.swing.DialogLogicTemplate;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;
import de.tinycodecrank.util.swing.events.GuiCloseEvent.Result;

final class BusinessLogic extends DialogLogicTemplate<ProjectGui, Void>
{
	private Opt<File> projectFolder = Opt.empty();
	private Opt<File> languageFolder = Opt.empty();
	private Opt<File> sourceFolder = Opt.empty();
	
	BusinessLogic(ProjectGui gui)
	{
		super(gui);
	}
	
	void abort(ActionEvent ae)
	{
		gui.if_(ProjectGui::dispose);
	}
	
	void apply(ActionEvent ae)
	{
		gui.if_(gui -> projectFolder.if_(pF ->
			{
				ProjectConfig project = new ProjectConfig(new File(pF, ProjectConfig.PROJECT_FILE_NAME));
				
				project.fileSettings.usePropertyFiles = gui.comboBoxFileType.getSelectedIndex() == 0;
				project.fileSettings.langFileExtension = gui.txtFileextension.getText();
				project.fileSettings.localizationDelimiter = gui.txtDelimiter.getText();
				project.fileSettings.versionListFile = gui.chckbxSaveVersionFile.isSelected();
				
				Path pP = pF.toPath();
				languageFolder.map(File::toPath)
					.if_(lP -> project.langFolder = relativize(pP, lP)
						.map(Path::toString)
						.map(path -> "./" + path)
						.get(() -> lP.toString()));
				
				sourceFolder.map(File::toPath)
					.if_(sP -> project.sourceFolder = relativize(pP, sP)
						.map(Path::toString)
						.map(path -> "./" + path)
						.get(() -> sP.toString()));

				gui.dispose(new GuiCloseEvent<>(Result.SUCCESS, Opt.empty(), project));
			}));
	}
	
	private Opt<Path> relativize(Path origin, Path target)
	{
		try
		{
			return Opt.of(origin.relativize(target));
		}
		catch(IllegalArgumentException e)
		{
			return Opt.empty();
		}
	}
	
	void selectProjectLocation(ActionEvent ae)
	{
		projectFolder = selectFolder(null);
		gui.filter(gui -> projectFolder != null)
			.if_(gui -> projectFolder.if_(pF -> gui.textProjectLocation.setText(pF.getPath())));
		validate();
	}
	
	void selectLangFiles(ActionEvent ae)
	{
		languageFolder = selectFolder(projectFolder.get(() -> null));
		gui.filter(gui -> projectFolder != null)
			.if_(gui ->
				languageFolder.map(lF -> lF.toPath())
				.if_(lP ->
				{
					String langFolder = projectFolder.map(File::toPath)
						.map(pP -> relativize(pP, lP)
							.map(Path::toString)
							.map(path -> "./" + path)
							.get(() -> lP.toString()))
						.get(() -> lP.toString());
					gui.textLanguageLocation.setText(langFolder);
				}));
		validate();
	}
	
	void selectSourceFiles(ActionEvent ae)
	{
		sourceFolder = selectFolder(projectFolder.get(() -> null));
		gui.filter(gui -> projectFolder != null)
			.if_(gui ->
				sourceFolder.map(sF -> sF.toPath())
				.if_(sP ->
				{
					String sourceFolder = projectFolder.map(File::toPath)
						.map(pP -> relativize(pP, sP)
							.map(Path::toString)
							.map(path -> "./" + path)
							.get(() -> sP.toString()))
						.get(() -> sP.toString());
					gui.textSourceLocation.setText(sourceFolder);
				}));
		validate();
	}
	
	private Opt<File> selectFolder(File startFolder)
	{
		return gui.flatmap(gui ->
		{
			JFileChooser chooser = new JFileChooser(startFolder);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int resultCode = chooser.showDialog(gui, "select");
			if(resultCode == JFileChooser.APPROVE_OPTION)
			{
				return Opt.of(chooser.getSelectedFile());
			}
			else
			{
				return Opt.empty();
			}
		});
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(gui ->
		{
			Localizer4J.prefs.newProjectWindow.height = gui.getHeight();
			Localizer4J.prefs.newProjectWindow.width = gui.getWidth();
			Localizer4J.prefs.newProjectWindow.posX = gui.getX();
			Localizer4J.prefs.newProjectWindow.posY = gui.getY();
		});
	}
	
	private void validate()
	{
		gui.if_(gui ->
		{
			boolean isValid = projectFolder != null && languageFolder != null;
			gui.applyAbortPanel.btnButton1.setEnabled(isValid);
		});
	}
	
	void fileTypeChanged(ActionEvent ae)
	{
		gui.if_(gui ->
		{
			int fileType = gui.comboBoxFileType.getSelectedIndex();
			switch(fileType)
			{
				case (0):
					gui.panel.setVisible(false);
					break;
				case (1):
					gui.panel.setVisible(true);
					break;
				default:
					break;
			}
		});
	}
}