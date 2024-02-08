package kryptonbutterfly.l4j.ui.lang;

import java.awt.event.ActionEvent;

import kryptonbutterfly.l4j.misc.Globals;
import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;

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
		gui.if_(Globals.windowStates.newLangWindow::persistBounds);
	}
}