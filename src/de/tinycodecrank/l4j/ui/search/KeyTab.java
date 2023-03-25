package de.tinycodecrank.l4j.ui.search;

import static de.tinycodecrank.l4j.ui.search.SearchGui.*;

import java.awt.BorderLayout;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.tinycodecrank.l4j.prefs.GuiPrefs;
import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.util.swing.ApplyAbortPanel;

final class KeyTab
{
	static JPanel buildKeyTab(
		SearchGui gui,
		Opt<BL> bL,
		GuiPrefs guiPrefs,
		BiConsumer<String, Consumer<String>> reg)
	{
		final var panelContent = Box.createVerticalBox();
		
		final var txtSearch = new JTextField();
		panelContent.add(txtSearch, BorderLayout.CENTER);
		
		final var	panelSettings		= Box.createVerticalBox();
		final var	chckbxCaseSensitive	= new JCheckBox();
		chckbxCaseSensitive.setRolloverEnabled(true);
		reg.accept("Search.Checkbox.Case Sensitive", chckbxCaseSensitive::setText);
		panelSettings.add(chckbxCaseSensitive);
		
		final var panel = new JPanel(new BorderLayout());
		panel.add(panelContent, BorderLayout.CENTER);
		panel.add(panelSettings, BorderLayout.EAST);
		
		bL.if_(bl ->
		{
			final var applyAbortPanel = new ApplyAbortPanel(
				buttonSearch,
				bl.search(txtSearch),
				buttonCancel,
				bl::abort);
			reg.accept(buttonSearch, applyAbortPanel.btnButton1::setText);
			reg.accept(buttonCancel, applyAbortPanel.btnButton2::setText);
			panel.add(applyAbortPanel, BorderLayout.SOUTH);
		});
		return panel;
	}
}