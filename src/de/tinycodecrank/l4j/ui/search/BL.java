package de.tinycodecrank.l4j.ui.search;

import java.awt.event.ActionEvent;
import java.util.function.BiFunction;

import de.tinycodecrank.functions.UnaryOperator;
import de.tinycodecrank.l4j.startup.Localizer4J;
import de.tinycodecrank.l4j.util.StringUtils;
import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.util.swing.DialogLogicTemplate;

final class BL extends DialogLogicTemplate<SearchGui, SearchKeyData>
{
	private SearchKeyData data;
	
	BL(SearchGui gui, SearchKeyData data)
	{
		super(gui);
		this.data = data;
	}
	
	void search(ActionEvent ae)
	{
		gui.if_(gui ->
		{
			final UnaryOperator<String> transformer = gui.keyTab.chckbxCaseSensitive.isSelected()
				? UnaryOperator.identity()
					: String::toLowerCase;
			
			BiFunction<String, String, Opt<Integer>> search = (left, right) -> StringUtils
				.matchOffset(left, transformer.apply(right));
			
			data.setSelection().accept(transformer.apply(gui.keyTab.txtSearch.getText()), search);
		});
	}
	
	void abort(ActionEvent ae)
	{
		gui.if_(gui -> gui.setVisible(false));
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(gui ->
		{
			Localizer4J.prefs.searchWindow.posX		= gui.getX();
			Localizer4J.prefs.searchWindow.posY		= gui.getY();
			Localizer4J.prefs.searchWindow.width	= gui.getWidth();
			Localizer4J.prefs.searchWindow.height	= gui.getHeight();
		});
	}
}