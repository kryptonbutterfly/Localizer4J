package de.tinycodecrank.l4j.ui.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.tinycodecrank.i18n.Localizer;
import de.tinycodecrank.l4j.data.gui.Translatable.TranslationState;
import de.tinycodecrank.l4j.data.persistence.Language;
import de.tinycodecrank.l4j.misc.Assets;
import de.tinycodecrank.l4j.prefs.GuiPrefs;
import de.tinycodecrank.l4j.startup.Localizer4J;
import de.tinycodecrank.l4j.startup.ProgramArgs;
import de.tinycodecrank.l4j.ui.main.parts.CountTableCellRenderer;
import de.tinycodecrank.l4j.ui.main.parts.TableModelClasses;
import de.tinycodecrank.l4j.ui.main.parts.TableModelMisc;
import de.tinycodecrank.l4j.ui.main.parts.TranslationTableCellRenderer;
import de.tinycodecrank.l4j.util.ColorUtils;
import de.tinycodecrank.l4j.util.ObservableLangGui;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public class MainGui extends ObservableLangGui<BusinessLogic, Void, Localizer>
{
	private static final String	countTitle		= "Main.table.count";
	private static final String	identifierTitle	= "Main.table.identifier";
	
	private static final String	lineTitle	= "Main.table.Line";
	private static final String	classTitle	= "Main.table.Class";
	private static final String	fileTitle	= "Main.table.File";
	private static final String	textTitle	= "Main.table.Text";
	
	JMenu				mnOpenRecent;
	JMenuItem			mnLanguage;
	private JMenuItem	mntmCreate;
	JMenuItem			mntmDelete;
	JMenuItem			mntmImport;
	private JMenuItem	mntmGeneralSettings;
	JMenuItem			mntmProjectSettings;
	JMenuItem			mntmSaveProject;
	
	JToggleButton pin;
	
	JComboBox<Language>	comboBoxLanguage;
	JComboBox<Language>	comboBoxFallback;
	
	JButton btnRemove;
	
	TableModelClasses	tableModelClasses	= new TableModelClasses(new String[] { lineTitle, classTitle });
	TableModelMisc		tableModelMisc		= new TableModelMisc(new String[] { lineTitle, fileTitle, textTitle });
	FilterTableModel	tableModel			= null;
	
	JTable		tableClasses;
	JTable		tableMisc;
	JTable		table;
	JScrollPane	tableScrollPane;
	
	JTextArea	txtAddKey;
	JButton		btnAddKey;
	
	JTextArea	txtTranslation;
	JTextArea	txtTranslationFallback;
	
	JButton btnSwap;
	
	JButton		btnApply;
	JSplitPane	splitPane;
	JSplitPane	splitPaneValue;
	JSplitPane	splitOccurences;
	
	boolean initialized = false;
	
	public MainGui(Consumer<GuiCloseEvent<Void>> closeListener, ProgramArgs args, Localizer l10n)
	{
		super(closeListener, l10n, l10n);
		
		final GuiPrefs guiPrefs = Localizer4J.prefs.mainWindow;
		
		reg(lineTitle, s -> tableModelClasses.setHeaderTitle(s, 0), s -> tableModelMisc.setHeaderTitle(s, 0));
		reg(classTitle, s -> tableModelClasses.setHeaderTitle(s, 1));
		reg(fileTitle, s -> tableModelMisc.setHeaderTitle(s, 1));
		reg(textTitle, s -> tableModelMisc.setHeaderTitle(s, 2));
		
		businessLogic.if_(bl ->
		{
			tableModel = new FilterTableModel(
				new String[] { countTitle, identifierTitle },
				t -> t.getTranslationState() != TranslationState.MISC_TRANSLATABLE);
			reg(countTitle, s -> tableModel.setHeaderTitle(s, 0));
			reg(identifierTitle, s -> tableModel.setHeaderTitle(s, 1));
		});
		
		setAlwaysOnTop(Localizer4J.prefs.pinnedOnTop);
		reg("Main.title", this::setTitle);
		setMinimumSize(new Dimension(600, 400));
		guiPrefs.setBoundsAndState(this);
		
		final ImageIcon	pinnedIcon		= ColorUtils
			.selectResize(getBackground(), Assets.PINNED_LIGHT, Assets.PINNED_DARK, 20, 20);
		final ImageIcon	unpinnedIcon	= ColorUtils
			.selectResize(getBackground(), Assets.UNPINNED_LIGHT, Assets.UNPINNED_DARK, 20, 20);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu();
		menuBar.add(mnFile);
		reg("Main.Menu.File", mnFile::setText);
		
		JMenuItem mntmNewProject = new JMenuItem();
		reg("Main.Menu.New", mntmNewProject::setText);
		mntmNewProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mntmNewProject);
		businessLogic.if_(bl -> mntmNewProject.addActionListener(bl::newProject));
		
		JMenuItem mntmOpenProject = new JMenuItem();
		reg("Main.Menu.Open", mntmOpenProject::setText);
		mntmOpenProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mntmOpenProject);
		businessLogic.if_(bl -> mntmOpenProject.addActionListener(bl::openProject));
		
		mnOpenRecent = new JMenu();
		reg("Main.Menu.Open Recent", mnOpenRecent::setText);
		mnFile.add(mnOpenRecent);
		
		mntmSaveProject = new JMenuItem();
		reg("Main.Menu.Save", mntmSaveProject::setText);
		mntmSaveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		mntmSaveProject.setEnabled(false);
		mnFile.add(mntmSaveProject);
		businessLogic.if_(bl -> mntmSaveProject.addActionListener(bl::saveProject));
		
		mnLanguage = new JMenu();
		reg("Main.Menu.Language", mnLanguage::setText);
		mnLanguage.setEnabled(false);
		menuBar.add(mnLanguage);
		
		mntmCreate = new JMenuItem();
		reg("Main.Menu.Create", mntmCreate::setText);
		mntmCreate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
		businessLogic.if_(bl -> mntmCreate.addActionListener(bl::createLanguage));
		mnLanguage.add(mntmCreate);
		
		mntmDelete = new JMenuItem();
		reg("Main.Menu.Delete", mntmDelete::setText);
		mntmDelete.setEnabled(false);
		mnLanguage.add(mntmDelete);
		businessLogic.if_(bl -> mntmDelete.addActionListener(bl::deleteLanguage));
		
		mntmImport = new JMenuItem();
		reg("Main.Menu.Import", mntmImport::setText);
		mntmImport.setEnabled(false);
		mnLanguage.add(mntmImport);
		businessLogic.if_(bl -> mntmImport.addActionListener(bl::importLanguage));
		
		JMenu mnOptions = new JMenu();
		reg("Main.Menu.Options", mnOptions::setText);
		menuBar.add(mnOptions);
		
		mntmGeneralSettings = new JMenuItem();
		reg("Main.Menu.General Settings", mntmGeneralSettings::setText);
		mnOptions.add(mntmGeneralSettings);
		businessLogic.if_(bl -> mntmGeneralSettings.addActionListener(bl::showGeneralOptions));
		
		mntmProjectSettings = new JMenuItem();
		reg("Main.Menu.Project Settings", mntmProjectSettings::setText);
		mnOptions.add(mntmProjectSettings);
		businessLogic.if_(bl -> mntmProjectSettings.addActionListener(bl::showProjectOptions));
		mntmProjectSettings.setEnabled(false);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		menuBar.add(horizontalGlue);
		
		pin = new JToggleButton(unpinnedIcon);
		reg("Main.Menu.Pin", pin::setToolTipText);
		pin.setSelectedIcon(pinnedIcon);
		pin.setRolloverEnabled(false);
		pin.setOpaque(false);
		pin.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		pin.setHorizontalAlignment(SwingConstants.TRAILING);
		pin.setSelected(Localizer4J.prefs.pinnedOnTop);
		menuBar.add(pin);
		businessLogic.if_(bl -> pin.addActionListener(bl::changePinned));
		
		// End Menu
		// Content
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		Box horizontalBox = Box.createHorizontalBox();
		contentPane.add(horizontalBox, BorderLayout.NORTH);
		
		JLabel lblLanguage = new JLabel();
		reg("Main.label.Language", lblLanguage::setText);
		horizontalBox.add(lblLanguage);
		
		comboBoxLanguage = new JComboBox<Language>();
		comboBoxLanguage.setEnabled(false);
		comboBoxLanguage.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		businessLogic.if_(bl -> comboBoxLanguage.addActionListener(bl::selectLanguage));
		horizontalBox.add(comboBoxLanguage);
		
		horizontalBox.add(Box.createHorizontalStrut(10));
		btnSwap = new JButton();
		reg("Main.button.swapLanguages", btnSwap::setText);
		reg("Main.button.swapLanguages_tooltip", btnSwap::setToolTipText);
		btnSwap.setEnabled(false);
		businessLogic.if_(bl -> btnSwap.addActionListener(bl::swapLanguages));
		horizontalBox.add(btnSwap);
		horizontalBox.add(Box.createHorizontalStrut(10));
		
		JLabel lblFallback = new JLabel();
		reg("Main.label.Fallback", lblFallback::setText);
		horizontalBox.add(lblFallback);
		
		comboBoxFallback = new JComboBox<Language>();
		comboBoxFallback.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		comboBoxFallback.setEnabled(false);
		businessLogic.if_(bl -> comboBoxFallback.addActionListener(bl::selectFallback));
		horizontalBox.add(comboBoxFallback);
		
		JPanel panelAll = new JPanel();
		panelAll.setLayout(new BorderLayout(0, 0));
		contentPane.add(panelAll, BorderLayout.CENTER);
		
		JPanel panelTable = new JPanel();
		panelTable.setLayout(new BorderLayout(0, 0));
		
		this.table = new JTable();
		table.setModel(this.tableModel);
		initTable(table, l10n);
		businessLogic.if_(bl -> table.getSelectionModel().addListSelectionListener(bl::tableSelectionListener));
		
		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setAutoscrolls(true);
		panelTable.add(tableScrollPane);
		panelTable.setMinimumSize(new Dimension(200, 10));
		
		JPanel panelAddKey = new JPanel();
		panelTable.add(panelAddKey, BorderLayout.SOUTH);
		txtAddKey = new JTextArea();
		txtAddKey.setWrapStyleWord(true);
		txtAddKey.setTabSize(10);
		txtAddKey.setEnabled(false);
		businessLogic.if_(bl -> txtAddKey.addKeyListener(bl.addKey()));
		
		btnAddKey = new JButton();
		reg("Main.button.add", btnAddKey::setText);
		btnAddKey.setEnabled(false);
		businessLogic.if_(bl -> btnAddKey.addActionListener(bl::addKey));
		panelAddKey.setLayout(new BorderLayout(0, 0));
		panelAddKey.add(txtAddKey);
		panelAddKey.add(btnAddKey, BorderLayout.EAST);
		
		JPanel panelTranslation = new JPanel();
		panelTranslation.setLayout(new BorderLayout(0, 0));
		
		JPanel panelValue = new JPanel();
		
		panelValue.setMinimumSize(new Dimension(100, 100));
		panelValue.setLayout(new BorderLayout(0, 0));
		
		Box horizontalBoxTitle = Box.createHorizontalBox();
		panelValue.add(horizontalBoxTitle, BorderLayout.NORTH);
		
		JPanel panelTitle = new JPanel();
		panelTitle.setLayout(new BorderLayout(0, 0));
		horizontalBoxTitle.add(panelTitle);
		
		JTextField labelTranslation = new JTextField();
		labelTranslation.setEditable(false);
		reg("Main.label.Translation", labelTranslation::setText);
		panelTitle.add(labelTranslation, BorderLayout.NORTH);
		labelTranslation.setColumns(10);
		
		JSeparator separatorAll = new JSeparator();
		panelTitle.add(separatorAll, BorderLayout.SOUTH);
		
		JPanel panelTitleFallBack = new JPanel();
		horizontalBoxTitle.add(panelTitleFallBack);
		panelTitleFallBack.setLayout(new BorderLayout(0, 0));
		
		JTextField labelTranslationFallback = new JTextField();
		reg("Main.label.TranslationFallback", labelTranslationFallback::setText);
		labelTranslationFallback.setEditable(false);
		labelTranslationFallback.setColumns(10);
		panelTitleFallBack.add(labelTranslationFallback, BorderLayout.NORTH);
		
		JSeparator spearator_1 = new JSeparator();
		panelTitleFallBack.add(spearator_1, BorderLayout.SOUTH);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelTable, panelTranslation);
		splitPane.setContinuousLayout(true);
		
		{
			tableClasses = new JTable();
			tableClasses.setModel(tableModelClasses);
			final var firstCol = tableClasses.getColumnModel().getColumn(0);
			firstCol.setResizable(false);
			firstCol.setPreferredWidth(40);
			firstCol.setMinWidth(40);
			firstCol.setMaxWidth(40);
			tableClasses.getColumnModel().getColumn(1).setResizable(false);
		}
		
		JScrollPane scrollPaneClasses = new JScrollPane(tableClasses);
		{
			tableMisc = new JTable();
			tableMisc.setModel(tableModelMisc);
			final var firstCol = tableMisc.getColumnModel().getColumn(0);
			firstCol.setResizable(false);
			firstCol.setPreferredWidth(40);
			firstCol.setMinWidth(40);
			firstCol.setMaxWidth(40);
		}
		JScrollPane scrollPaneMisc = new JScrollPane(tableMisc);
		
		splitOccurences = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneClasses, scrollPaneMisc);
		splitOccurences.setContinuousLayout(true);
		
		splitPaneValue = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelValue, splitOccurences);
		splitPaneValue.setContinuousLayout(true);
		
		JPanel panelTranslationGrid = new JPanel();
		panelValue.add(panelTranslationGrid, BorderLayout.CENTER);
		panelTranslationGrid.setLayout(new GridLayout(0, 2, 3, 3));
		
		txtTranslation = new JTextArea();
		txtTranslation.setWrapStyleWord(true);
		txtTranslation.setLineWrap(true);
		panelTranslationGrid.add(txtTranslation);
		txtTranslation.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				btnApply.setEnabled(true);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				btnApply.setEnabled(true);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				btnApply.setEnabled(true);
			}
		});
		businessLogic.if_(bl -> txtTranslation.addKeyListener(bl.changeTranslation()));
		
		txtTranslationFallback = new JTextArea();
		txtTranslationFallback.setWrapStyleWord(true);
		txtTranslationFallback.setLineWrap(true);
		txtTranslationFallback.setEditable(false);
		panelTranslationGrid.add(txtTranslationFallback);
		
		JPanel		panel		= new JPanel();
		FlowLayout	flowLayout	= (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panelValue.add(panel, BorderLayout.SOUTH);
		
		btnApply = new JButton();
		reg("Main.button.apply", btnApply::setText);
		btnApply.setEnabled(false);
		panel.add(btnApply);
		businessLogic.if_(bl -> btnApply.addActionListener(bl::changeTranslation));
		
		panelTranslation.add(splitPaneValue, BorderLayout.CENTER);
		
		panelAll.add(splitPane, BorderLayout.CENTER);
		
		Box vertBox = Box.createVerticalBox();
		panelAll.add(vertBox, BorderLayout.SOUTH);
		
		JPanel panel_2 = new JPanel();
		vertBox.add(panel_2);
		((FlowLayout) panel_2.getLayout()).setAlignment(FlowLayout.TRAILING);
		
		btnRemove = new JButton();
		reg("Main.button.remove", btnRemove::setText);
		btnRemove.setEnabled(false);
		panel_2.add(btnRemove);
		businessLogic.if_(bl -> btnRemove.addActionListener(bl::removeButton));
		
		businessLogic.if_(bl ->
		{
			bl.updateRecent();
			bl.init(args);
		});
		
		splitPane.setDividerLocation(Localizer4J.prefs.layout.keyTableWidth);
		splitPaneValue.setDividerLocation(Localizer4J.prefs.layout.translationHeight);
		splitOccurences.setDividerLocation(Localizer4J.prefs.layout.occurenceHeight);
		
		initialized = true;
	}
	
	Localizer getLocalizer()
	{
		return langManager.localizer;
	}
	
	@Override
	protected BusinessLogic createBusinessLogic(Localizer l10n)
	{
		return new BusinessLogic(this, l10n);
	}
	
	private static void initTable(JTable table, Localizer l10n)
	{
		final var firstCol = table.getColumnModel().getColumn(0);
		firstCol.setPreferredWidth(34);
		firstCol.setMinWidth(34);
		firstCol.setMaxWidth(34);
		firstCol.setCellRenderer(new CountTableCellRenderer());
		table.getColumnModel().getColumn(1).setCellRenderer(new TranslationTableCellRenderer(l10n));
		table.setOpaque(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	}
}