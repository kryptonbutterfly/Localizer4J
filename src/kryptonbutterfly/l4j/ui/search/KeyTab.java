package kryptonbutterfly.l4j.ui.search;

import static kryptonbutterfly.l4j.ui.search.SearchGui.*;

import java.awt.BorderLayout;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.util.swing.ApplyAbortPanel;

@SuppressWarnings("serial")
final class KeyTab extends JPanel implements RefocusableTab
{
	final JTextField	txtSearch;
	final JCheckBox		chckbxCaseSensitive;
	
	KeyTab(SearchGui gui, Opt<BL> bL, BiConsumer<String, Consumer<String>> reg)
	{
		super(new BorderLayout());
		
		final var panelContent = Box.createVerticalBox();
		
		txtSearch = new JTextField();
		panelContent.add(txtSearch, BorderLayout.CENTER);
		
		final var panelSettings = Box.createVerticalBox();
		chckbxCaseSensitive = new JCheckBox();
		chckbxCaseSensitive.setRolloverEnabled(true);
		reg.accept("Search.Checkbox.Case Sensitive", chckbxCaseSensitive::setText);
		panelSettings.add(chckbxCaseSensitive);
		
		add(panelContent, BorderLayout.CENTER);
		add(panelSettings, BorderLayout.EAST);
		
		bL.if_(bl -> {
			final var	applyAbortPanel	= new ApplyAbortPanel(
				buttonSearch,
				bl::search,
				buttonCancel,
				bl::abort);
			final var	btnSearch		= applyAbortPanel.btnButton1;
			final var	btnCancel		= applyAbortPanel.btnButton2;
			
			reg.accept(buttonSearch, btnSearch::setText);
			reg.accept(buttonCancel, btnCancel::setText);
			
			txtSearch.addKeyListener(bl.escapeListener);
			txtSearch.addKeyListener(bl.findEnterListener);
			chckbxCaseSensitive.addKeyListener(bl.escapeListener);
			btnSearch.addKeyListener(bl.escapeListener);
			btnCancel.addKeyListener(bl.escapeListener);
			
			add(applyAbortPanel, BorderLayout.SOUTH);
		});
	}
	
	@Override
	public void refocus()
	{
		txtSearch.requestFocus();
	}
}