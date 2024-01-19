package kryptonbutterfly.l4j.ui.settings;

import java.awt.event.ActionEvent;

import kryptonbutterfly.l4j.prefs.FileSettings;
import kryptonbutterfly.l4j.prefs.FileType.LocalizingFileType;
import kryptonbutterfly.l4j.startup.Localizer4J;
import kryptonbutterfly.util.swing.Logic;

final class BusinessLogic extends Logic<Settings, FileSettings>
{
	private final FileSettings fileSettings;
	
	BusinessLogic(Settings gui, FileSettings settings)
	{
		super(gui);
		this.fileSettings = settings;
	}
	
	void fileTypeChanged(ActionEvent event)
	{
		gui.if_(
			gui -> gui.panelSettingsLang
				.setVisible(((LocalizingFileType) gui.comboBoxFileType.getSelectedItem()).type().varExtension()));
	}
	
	void setLanguage(ActionEvent event)
	{
		gui.if_(gui -> {
			final var uiLang = (String) gui.comboBoxUiLanguage.getSelectedItem();
			gui.localizer().setCurrentLanguage(uiLang);
		});
	}
	
	void buttonOK(ActionEvent event)
	{
		final String DOT = ".";
		gui.if_(gui -> {
			String langFileExtension = gui.textField.getText().trim();
			if (!langFileExtension.startsWith(DOT))
				langFileExtension = DOT + langFileExtension;
			
			fileSettings.languageFileType		= ((LocalizingFileType) gui.comboBoxFileType.getSelectedItem()).type();
			fileSettings.langFileExtension		= langFileExtension;
			fileSettings.localizationDelimiter	= gui.textField_1.getText().trim();
			fileSettings.versionListFile		= gui.chckbxSaveVersionFile.isSelected();
			if (gui.isGeneral)
			{
				Localizer4J.prefs.history.maxLength = (int) gui.spinnerHistoryLength.getModel().getValue();
				
				while (Localizer4J.prefs.history.recent.size() > Localizer4J.prefs.history.maxLength)
					Localizer4J.prefs.history.recent.removeLast();
				
				Localizer4J.prefs.language = gui.localizer().currentLanguage();
			}
			gui.dispose();
		});
	}
	
	void buttonCancle(ActionEvent event)
	{
		gui.if_(gui -> {
			gui.localizer().setCurrentLanguage(Localizer4J.prefs.language);
			gui.dispose();
		});
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(gui -> {
			Localizer4J.prefs.settingsWindow.posX	= gui.getX();
			Localizer4J.prefs.settingsWindow.posY	= gui.getY();
			Localizer4J.prefs.save();
		});
	}
}