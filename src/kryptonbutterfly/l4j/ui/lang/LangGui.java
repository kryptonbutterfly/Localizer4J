package kryptonbutterfly.l4j.ui.lang;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import kryptonbutterfly.i18n.Localizer;
import kryptonbutterfly.l4j.startup.Localizer4J;
import kryptonbutterfly.l4j.util.ObservableLangDialog;
import kryptonbutterfly.util.swing.ApplyAbortPanel;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public class LangGui extends ObservableLangDialog<BusinessLogic, String, Void>
{
	private static final String	buttonAdd		= "CreateLanguage.button.accept";
	private static final String	buttonCancle	= "CreateLanguage.button.cancle";
	
	JTextField txtLang;
	
	public LangGui(Window owner, ModalityType modality, Consumer<GuiCloseEvent<String>> closeListener, Localizer l10n)
	{
		super(owner, modality, closeListener, l10n);
		Localizer4J.prefs.newLangWindow.setBounds(this);
		setResizable(false);
		
		JPanel panelContent = new JPanel();
		getContentPane().add(panelContent, BorderLayout.NORTH);
		GridBagLayout gbl_panelContent = new GridBagLayout();
		gbl_panelContent.columnWidths	= new int[] { 0, 0 };
		gbl_panelContent.rowHeights		= new int[] { 0 };
		gbl_panelContent.columnWeights	= new double[] { 0.0, 1.0 };
		gbl_panelContent.rowWeights		= new double[] { 1.0 };
		panelContent.setLayout(gbl_panelContent);
		
		JLabel lblLang = new JLabel();
		reg("CreateLanguage.label.Language", lblLang::setText);
		lblLang.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblLang = new GridBagConstraints();
		gbc_lblLang.fill	= GridBagConstraints.VERTICAL;
		gbc_lblLang.anchor	= GridBagConstraints.WEST;
		gbc_lblLang.insets	= new Insets(0, 0, 0, 5);
		gbc_lblLang.gridx	= 0;
		gbc_lblLang.gridy	= 0;
		panelContent.add(lblLang, gbc_lblLang);
		
		txtLang = new JTextField();
		GridBagConstraints gbc_txtLang = new GridBagConstraints();
		gbc_txtLang.fill	= GridBagConstraints.HORIZONTAL;
		gbc_txtLang.gridx	= 1;
		gbc_txtLang.gridy	= 0;
		panelContent.add(txtLang, gbc_txtLang);
		txtLang.setColumns(10);
		businessLogic.if_(bl -> {
			ApplyAbortPanel aaPanel = new ApplyAbortPanel(buttonAdd, bl::add, buttonCancle, bl::cancle);
			reg(buttonAdd, aaPanel.btnButton1::setText);
			reg(buttonCancle, aaPanel.btnButton2::setText);
			getContentPane().add(aaPanel, BorderLayout.SOUTH);
		});
		
		setVisible(true);
	}
	
	@Override
	protected BusinessLogic createBusinessLogic(Void args)
	{
		return new BusinessLogic(this);
	}
}