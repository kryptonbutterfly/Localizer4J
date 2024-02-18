package kryptonbutterfly.l4j.ui.settings;

import java.awt.event.ActionEvent;

import kryptonbutterfly.l4j.misc.Globals;
import kryptonbutterfly.l4j.prefs.FileSettings;
import kryptonbutterfly.l4j.prefs.FileType.LocalizingFileType;
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
				Globals.prefs.history.maxLength = (int) gui.spinnerHistoryLength.getModel().getValue();
				
				while (Globals.prefs.history.recent.size() > Globals.prefs.history.maxLength)
					Globals.prefs.history.recent.removeLast();
				
				Globals.prefs.language = gui.localizer().currentLanguage();
			}
			gui.dispose();
		});
	}
	
	void buttonCancle(ActionEvent event)
	{
		gui.if_(gui -> {
			gui.localizer().setCurrentLanguage(Globals.prefs.language);
			gui.dispose();
		});
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(Globals.windowStates.settingsWindow::persistBounds);
	}
}