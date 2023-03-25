package de.tinycodecrank.l4j.ui.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import de.tinycodecrank.l4j.startup.Localizer4J;
import de.tinycodecrank.util.swing.DialogLogicTemplate;

final class BL extends DialogLogicTemplate<SearchGui, SearchKeyData>
{
	private SearchKeyData data;
	
	BL(SearchGui gui, SearchKeyData data)
	{
		super(gui);
		this.data = data;
	}
	
	ActionListener search(JTextField text)
	{
		return ae -> data.setSelection().accept(text.getText());
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