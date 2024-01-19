package kryptonbutterfly.l4j.util;

import java.awt.Window;
import java.util.function.Consumer;

import kryptonbutterfly.i18n.Localizer;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public abstract class ObservableLangDialog<BL extends Logic<?, Args>, R, Args> extends ObservableDialog<BL, R, Args>
{
	protected final LangManager langManager;
	
	public ObservableLangDialog(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<R>> closeListener,
		Localizer localizer)
	{
		super(owner, modality, closeListener);
		this.langManager = new LangManager(localizer);
	}
	
	public ObservableLangDialog(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<R>> closeListener,
		Localizer localizer,
		Args args)
	{
		super(owner, modality, closeListener, args);
		this.langManager = new LangManager(localizer);
	}
	
	@SafeVarargs
	protected final void reg(String key, Consumer<String>... listener)
	{
		this.langManager.reg(key, listener);
	}
	
	public final Localizer localizer()
	{
		return this.langManager.localizer;
	}
	
	@Override
	public void disposeAction()
	{
		this.langManager.close();
	}
}