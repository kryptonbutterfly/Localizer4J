package kryptonbutterfly.l4j.ui.search;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.BiFunction;

import kryptonbutterfly.functions.UnaryOperator;
import kryptonbutterfly.l4j.misc.Globals;
import kryptonbutterfly.l4j.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.l4j.util.StringUtils;
import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.util.swing.Logic;

final class BL extends Logic<SearchGui, SearchKeyData>
{
	final KeyListener findEnterListener = new KeyTypedAdapter(
		c -> search(null),
		KeyEvent.VK_ENTER);
	
	final KeyListener escapeListener = new KeyTypedAdapter(
		c -> abort(null),
		KeyEvent.VK_ESCAPE);
	
	private SearchKeyData data;
	
	BL(SearchGui gui, SearchKeyData data)
	{
		super(gui);
		this.data = data;
	}
	
	void search(ActionEvent ae)
	{
		gui.if_(gui -> {
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
		gui.if_(gui -> {
			Globals.windowStates.searchWindow.persistBounds(gui);
		});
	}
}