package de.tinycodecrank.l4j.ui.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.tinycodecrank.i18n.Localizer;
import de.tinycodecrank.l4j.prefs.FileSettings;
import de.tinycodecrank.l4j.prefs.GuiPrefs;
import de.tinycodecrank.l4j.startup.Localizer4J;
import de.tinycodecrank.l4j.util.ObservableLangDialog;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public class Settings extends ObservableLangDialog<BusinessLogic, Void, FileSettings>
{
	private final FileSettings guiPrefs;
	
	private JPanel		contentPane;
	JTextField			textField;
	JTextField			textField_1;
	JPanel				panelSettingsLang;
	JComboBox<String>	comboBoxFileType;
	JSpinner			spinnerHistoryLength;
	JCheckBox			chckbxSaveVersionFile;
	JComboBox<String>	comboBoxUiLanguage;
	
	public Settings(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<Void>> closeListener,
		FileSettings fileSettings,
		Localizer l10n)
	{
		super(owner, modality, closeListener, l10n, fileSettings);
		this.guiPrefs = fileSettings;
		GuiPrefs prefs = Localizer4J.prefs.settingsWindow;
		
		reg("Settings.title", this::setTitle);
		
		setBounds(prefs.posX, prefs.posY, 450, 260);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panelSettings = new JPanel();
		contentPane.add(panelSettings, BorderLayout.NORTH);
		panelSettings.setLayout(new BorderLayout(0, 0));
		
		JPanel panelSettingsType = new JPanel();
		panelSettings.add(panelSettingsType, BorderLayout.NORTH);
		panelSettingsType.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panelSettingsType.add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblFileType = new JLabel();
		reg("Settings.label.File type", lblFileType::setText);
		panel.add(lblFileType);
		
		comboBoxFileType = new JComboBox<>();
		comboBoxFileType.setModel(new DefaultComboBoxModel<String>(new String[] { "Properties", "Lang" }));
		comboBoxFileType.setSelectedIndex(fileSettings.usePropertyFiles ? 0 : 1);
		businessLogic.if_(bl -> comboBoxFileType.addActionListener(bl::fileTypeChanged));
		panel.add(comboBoxFileType);
		
		JLabel label_2 = new JLabel();
		reg("Settings.label.Save to lang_versions.xml", label_2::setText);
		panel.add(label_2);
		chckbxSaveVersionFile = new JCheckBox("");
		chckbxSaveVersionFile.setSelected(fileSettings.versionListFile);
		chckbxSaveVersionFile.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(chckbxSaveVersionFile);
		
		JSeparator separator = new JSeparator();
		panelSettingsType.add(separator, BorderLayout.SOUTH);
		
		panelSettingsLang = new JPanel();
		panelSettingsLang.setVisible(!fileSettings.usePropertyFiles);
		panelSettings.add(panelSettingsLang, BorderLayout.CENTER);
		panelSettingsLang.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel label = new JLabel();
		reg("Settings.label.Language file extension", label::setText);
		reg("Settings.label.tooltip.Language file extension", label::setToolTipText);
		panelSettingsLang.add(label);
		
		textField = new JTextField();
		reg("Settings.label.tooltip.Language file extension", textField::setToolTipText);
		textField.setText(guiPrefs.langFileExtension);
		textField.setHorizontalAlignment(SwingConstants.TRAILING);
		textField.setColumns(10);
		panelSettingsLang.add(textField);
		
		JLabel label_1 = new JLabel();
		reg("Settings.label.LocalizationDelimiter", label_1::setText);
		reg("Settings.label.tooltip.LocalizationDelimiter", label_1::setToolTipText);
		panelSettingsLang.add(label_1);
		
		textField_1 = new JTextField();
		textField_1.setText(guiPrefs.localizationDelimiter);
		textField_1.setHorizontalAlignment(SwingConstants.TRAILING);
		textField_1.setColumns(10);
		panelSettingsLang.add(textField_1);
		
		JPanel panel_1 = new JPanel();
		panelSettings.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JSeparator separator_2 = new JSeparator();
		panel_1.add(separator_2, BorderLayout.NORTH);
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblHistoryMaxLength = new JLabel();
		reg("Settings.label.History max length", lblHistoryMaxLength::setText);
		panel_2.add(lblHistoryMaxLength);
		
		spinnerHistoryLength = new JSpinner();
		spinnerHistoryLength.setModel(new SpinnerNumberModel(Localizer4J.prefs.history.maxLength, 0, 20, 1));
		panel_2.add(spinnerHistoryLength);
		
		JLabel lblUILanguage = new JLabel();
		reg("Settings.label.UILanguage", lblUILanguage::setText);
		panel_2.add(lblUILanguage);
		
		comboBoxUiLanguage = new JComboBox<>(l10n.manager.languages());
		comboBoxUiLanguage.setSelectedItem(l10n.currentLanguage());
		panel_2.add(comboBoxUiLanguage);
		businessLogic.if_(bl -> comboBoxUiLanguage.addActionListener(bl::setLanguage));
		
		JPanel panelOkAbort = new JPanel();
		contentPane.add(panelOkAbort, BorderLayout.SOUTH);
		panelOkAbort.setLayout(new BorderLayout(0, 0));
		
		JSeparator separator_1 = new JSeparator();
		panelOkAbort.add(separator_1, BorderLayout.NORTH);
		
		JPanel panelConfirmButtons = new JPanel();
		panelOkAbort.add(panelConfirmButtons, BorderLayout.CENTER);
		FlowLayout flowLayout = (FlowLayout) panelConfirmButtons.getLayout();
		flowLayout.setAlignment(FlowLayout.TRAILING);
		
		JButton btnOk = new JButton();
		reg("Settings.button.ok", btnOk::setText);
		panelConfirmButtons.add(btnOk);
		businessLogic.if_(bl -> btnOk.addActionListener(bl::buttonOK));
		
		JButton btnCancle = new JButton();
		reg("Settings.button.cancle", btnCancle::setText);
		panelConfirmButtons.add(btnCancle);
		businessLogic.if_(bl -> btnCancle.addActionListener(bl::buttonCancle));
		
		this.setVisible(true);
	}
	
	@Override
	protected BusinessLogic createBusinessLogic(FileSettings settings)
	{
		return new BusinessLogic(this, settings);
	}
}