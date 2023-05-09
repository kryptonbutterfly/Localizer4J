package de.tinycodecrank.l4j.ui.lang;

import java.awt.event.ActionEvent;

import de.tinycodecrank.l4j.startup.Localizer4J;
import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.util.swing.Logic;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;
import de.tinycodecrank.util.swing.events.GuiCloseEvent.Result;

final class BusinessLogic extends Logic<LangGui, Void>
{
	BusinessLogic(LangGui gui)
	{
		super(gui);
	}
	
	void add(ActionEvent ae)
	{
		gui.if_(gui -> gui.dispose(new GuiCloseEvent<String>(Result.SUCCESS, Opt.empty(), gui.txtLang.getText())));
	}
	
	void cancle(ActionEvent ae)
	{
		gui.if_(LangGui::dispose);
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(gui ->
		{
			Localizer4J.prefs.newLangWindow.posX	= gui.getX();
			Localizer4J.prefs.newLangWindow.posY	= gui.getY();
		});
	}
}