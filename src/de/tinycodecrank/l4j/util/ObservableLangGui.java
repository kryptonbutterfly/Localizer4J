package de.tinycodecrank.l4j.util;

import java.util.function.Consumer;

import de.tinycodecrank.i18n.Localizer;
import de.tinycodecrank.util.swing.BusinessLogicTemplate;
import de.tinycodecrank.util.swing.ObservableGui;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;

public abstract class ObservableLangGui<Logic extends BusinessLogicTemplate<?, Args>, R, Args> extends ObservableGui<Logic, R, Args>
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
	protected final void reg(String key, Consumer<String> ... listener)
	{
		this.langManager.reg(key, listener);
	}
	
	@Override
	public void disposeAction()
	{
		langManager.close();
	}
	
}