package kryptonbutterfly.l4j.util;

import java.util.function.Consumer;

import kryptonbutterfly.i18n.Localizer;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.ObservableGui;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

public abstract class ObservableLangGui<BL extends Logic<?, Args>, R, Args> extends ObservableGui<BL, R, Args>
{
	private static final long serialVersionUID = 1L;
	
	public final LangManager langManager;
	
	public ObservableLangGui(Consumer<GuiCloseEvent<R>> closeListener, Localizer localizer)
	{
		super(closeListener);
		this.langManager = new LangManager(localizer);
	}
	
	public ObservableLangGui(Consumer<GuiCloseEvent<R>> closeListener, Localizer localizer, Args args)
	{
		super(closeListener, args);
		this.langManager = new LangManager(localizer);
	}
	
	@SafeVarargs
	protected final void reg(String key, Consumer<String>... listener)
	{
		this.langManager.reg(key, listener);
	}
	
	@Override
	public void disposeAction()
	{
		langManager.close();
	}
}