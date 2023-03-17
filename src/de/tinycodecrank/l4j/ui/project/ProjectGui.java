package de.tinycodecrank.l4j.ui.project;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import de.tinycodecrank.i18n.Localizer;
import de.tinycodecrank.l4j.config.ProjectConfig;
import de.tinycodecrank.l4j.prefs.FileType;
import de.tinycodecrank.l4j.prefs.FileType.LocalizingFileType;
import de.tinycodecrank.l4j.startup.Localizer4J;
import de.tinycodecrank.l4j.util.UpdateableComboBoxModel;
import de.tinycodecrank.l4j.util.ObservableLangDialog;
import de.tinycodecrank.util.swing.ApplyAbortPanel;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public class ProjectGui extends ObservableLangDialog<BusinessLogic, ProjectConfig, Void>
{
	private static final String	languageTitleBorderText	= "New Project.titleBorder.languages";
	private static final String	sourcesTitleBorderText	= "New Project.titleBorder.sources";
	private static final String	buttonAccept			= "New Project.button.accept";
	private static final String	buttonCancel			= "New Project.button.cancle";
	
	JTextField	textProjectLocation;
	JTextField	textLanguageLocation;
	JTextField	textSourceLocation;
	
	ApplyAbortPanel applyAbortPanel;
	
	JTextField	txtFileextension;
	JTextField	txtDelimiter;
	
	JPanel							panel;
	JComboBox<LocalizingFileType>	comboBoxFileType;
	JCheckBox						chckbxSaveVersionFile;
	
	private final LocalizingFileType[]			fileTypes;
	private final BiConsumer<String, String>	langChangeListener;
	
	public ProjectGui(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<ProjectConfig>> closeListener,
		Localizer l10n)
	{
		super(owner, modality, closeListener, l10n);
		BorderLayout borderLayout = (BorderLayout) getContentPane().getLayout();
		borderLayout.setVgap(5);
		borderLayout.setHgap(5);
		
		Localizer4J.prefs.newProjectWindow.setBounds(this);
		reg("New Project.title", this::setTitle);
		
		JPanel panel_1 = new JPanel();
		
		JScrollPane scrollPane = new JScrollPane(panel_1);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(5, 5));
		
		Box verticalBox = Box.createVerticalBox();
		panel_1.add(verticalBox);
		
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		
		JLabel lblLanguageLocation = new JLabel();
		reg("New Project.label.Location", lblLanguageLocation::setText);
		horizontalBox.add(lblLanguageLocation);
		lblLanguageLocation.setHorizontalAlignment(SwingConstants.CENTER);
		
		textProjectLocation = new JTextField();
		textProjectLocation.setEditable(false);
		textProjectLocation.setMaximumSize(new Dimension(2147483647, 35));
		horizontalBox.add(textProjectLocation);
		textProjectLocation.setColumns(10);
		
		JButton btnBrowse = new JButton();
		reg("New Project.button.browse", btnBrowse::setText);
		btnBrowse.setMaximumSize(new Dimension(94, 35));
		horizontalBox.add(btnBrowse);
		businessLogic.if_(bl -> btnBrowse.addActionListener(bl::selectProjectLocation));
		
		Component verticalStrut_1 = Box.createVerticalStrut(15);
		verticalBox.add(verticalStrut_1);
		
		Box				horizontalBox_2		= Box.createHorizontalBox();
		TitledBorder	languageTitleBorder	= BorderFactory.createTitledBorder(languageTitleBorderText);
		reg(languageTitleBorderText, languageTitleBorder::setTitle);
		horizontalBox_2.setBorder(languageTitleBorder);
		verticalBox.add(horizontalBox_2);
		
		Box verticalBox_1 = Box.createVerticalBox();
		horizontalBox_2.add(verticalBox_1);
		
		Box horizontalBox_4 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_4);
		
		JLabel lblFileType = new JLabel();
		reg("New Project.label.File type", lblFileType::setText);
		horizontalBox_4.add(lblFileType);
		
		comboBoxFileType	= new JComboBox<>();
		fileTypes			= FileType.localized(langManager);
		final var typesModel = new UpdateableComboBoxModel<>(fileTypes);
		comboBoxFileType.setModel(typesModel);
		comboBoxFileType.setSelectedItem(FileType.match(Localizer4J.prefs.fileSettings.languageFileType, fileTypes));
		langChangeListener = (s1, s2) -> typesModel.fireChange();
		l10n.addLanguageChangeListener(langChangeListener);
		comboBoxFileType.setMaximumSize(new Dimension(32767, 32));
		businessLogic.if_(bl -> comboBoxFileType.addActionListener(bl::fileTypeChanged));
		horizontalBox_4.add(comboBoxFileType);
		
		Component verticalStrut = Box.createVerticalStrut(5);
		verticalBox_1.add(verticalStrut);
		
		panel = new JPanel();
		panel.setVisible(false);
		panel.setMaximumSize(new Dimension(32767, 70));
		verticalBox_1.add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblFileExtension = new JLabel();
		reg("New Project.label.File extension", lblFileExtension::setText);
		panel.add(lblFileExtension);
		
		txtFileextension = new JTextField();
		txtFileextension.setText(".lang");
		panel.add(txtFileextension);
		txtFileextension.setColumns(10);
		
		JLabel lblLocalizationDelimiter = new JLabel();
		reg("New Project.label.Localization delimiter", lblLocalizationDelimiter::setText);
		panel.add(lblLocalizationDelimiter);
		
		txtDelimiter = new JTextField();
		txtDelimiter.setText("=");
		panel.add(txtDelimiter);
		txtDelimiter.setColumns(10);
		
		Component verticalStrut_2 = Box.createVerticalStrut(5);
		verticalBox_1.add(verticalStrut_2);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setMaximumSize(new Dimension(32767, 1));
		verticalBox_1.add(separator_1);
		
		Component verticalStrut_3 = Box.createVerticalStrut(5);
		verticalBox_1.add(verticalStrut_3);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_1);
		
		JLabel lblLocation = new JLabel();
		reg("New Project.label.Location", lblLocation::setText);
		horizontalBox_1.add(lblLocation);
		
		textLanguageLocation = new JTextField();
		horizontalBox_1.add(textLanguageLocation);
		textLanguageLocation.setEditable(false);
		textLanguageLocation.setMaximumSize(new Dimension(2147483647, 35));
		textLanguageLocation.setColumns(10);
		
		JButton btnBrowse_1 = new JButton();
		reg("New Project.button.browse", btnBrowse_1::setText);
		horizontalBox_1.add(btnBrowse_1);
		btnBrowse_1.setMaximumSize(new Dimension(94, 35));
		
		Component verticalStrut_4 = Box.createVerticalStrut(5);
		verticalBox_1.add(verticalStrut_4);
		
		JPanel panel_2 = new JPanel();
		verticalBox_1.add(panel_2);
		panel_2.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblNewLabel = new JLabel();
		reg("New Project.label.Create lang_version.xml", lblNewLabel::setText);
		panel_2.add(lblNewLabel);
		
		chckbxSaveVersionFile = new JCheckBox("");
		panel_2.add(chckbxSaveVersionFile);
		chckbxSaveVersionFile.setHorizontalAlignment(SwingConstants.RIGHT);
		businessLogic.if_(bl -> btnBrowse_1.addActionListener(bl::selectLangFiles));
		
		Box				horizontalBox_3		= Box.createHorizontalBox();
		TitledBorder	sourceTitledBorder	= BorderFactory.createTitledBorder(sourcesTitleBorderText);
		reg(sourcesTitleBorderText, sourceTitledBorder::setTitle);
		horizontalBox_3.setBorder(sourceTitledBorder);
		verticalBox.add(horizontalBox_3);
		
		JLabel lblLocation_1 = new JLabel();
		reg("New Project.label.Location", lblLocation_1::setText);
		horizontalBox_3.add(lblLocation_1);
		
		textSourceLocation = new JTextField();
		textSourceLocation.setEditable(false);
		textSourceLocation.setMaximumSize(new Dimension(2147483647, 35));
		horizontalBox_3.add(textSourceLocation);
		textSourceLocation.setColumns(10);
		
		JButton btnBrowse_2 = new JButton();
		reg("New Project.button.browse", btnBrowse_2::setText);
		btnBrowse_2.setMaximumSize(new Dimension(94, 35));
		horizontalBox_3.add(btnBrowse_2);
		businessLogic.if_(bl -> btnBrowse_2.addActionListener(bl::selectSourceFiles));
		
		businessLogic.if_(bl ->
		{
			applyAbortPanel = new ApplyAbortPanel(buttonAccept, bl::apply, buttonCancel, bl::abort);
			reg(buttonAccept, applyAbortPanel.btnButton1::setText);
			reg(buttonCancel, applyAbortPanel.btnButton2::setText);
			getContentPane().add(applyAbortPanel, BorderLayout.SOUTH);
			applyAbortPanel.btnButton1.setEnabled(false);
		});
		
		setVisible(true);
	}
	
	@Override
	protected BusinessLogic createBusinessLogic(Void args)
	{
		return new BusinessLogic(this);
	}
	
	@Override
	public void disposeAction()
	{
		langManager.localizer.removeLanguageChangeListener(langChangeListener);
		super.disposeAction();
	}
}