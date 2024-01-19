package kryptonbutterfly.l4j.ui.search;

import static kryptonbutterfly.l4j.ui.search.SearchGui.*;

import java.awt.BorderLayout;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kryptonbutterfly.l4j.prefs.GuiPrefs;
import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.util.swing.ApplyAbortPanel;

final class KeyTab
{
	final JTextField	txtSearch;
	final JPanel		panel;
	final JCheckBox		chckbxCaseSensitive;
	
	KeyTab(SearchGui gui, Opt<BL> bL, GuiPrefs guiPrefs, BiConsumer<String, Consumer<String>> reg)
	{
		final var panelContent = Box.createVerticalBox();
		
		txtSearch = new JTextField();
		panelContent.add(txtSearch, BorderLayout.CENTER);
		
		final var panelSettings = Box.createVerticalBox();
		chckbxCaseSensitive = new JCheckBox();
		chckbxCaseSensitive.setRolloverEnabled(true);
		reg.accept("Search.Checkbox.Case Sensitive", chckbxCaseSensitive::setText);
		panelSettings.add(chckbxCaseSensitive);
		
		panel = new JPanel(new BorderLayout());
		panel.add(panelContent, BorderLayout.CENTER);
		panel.add(panelSettings, BorderLayout.EAST);
		
		bL.if_(bl -> {
			final var applyAbortPanel = new ApplyAbortPanel(
				buttonSearch,
				bl::search,
				buttonCancel,
				bl::abort);
			reg.accept(buttonSearch, applyAbortPanel.btnButton1::setText);
			reg.accept(buttonCancel, applyAbortPanel.btnButton2::setText);
			panel.add(applyAbortPanel, BorderLayout.SOUTH);
		});
	}
}