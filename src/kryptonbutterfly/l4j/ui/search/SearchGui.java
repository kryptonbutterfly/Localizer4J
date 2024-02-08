package kryptonbutterfly.l4j.ui.search;

import java.awt.Dimension;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.JTabbedPane;

import kryptonbutterfly.i18n.Localizer;
import kryptonbutterfly.l4j.misc.Globals;
import kryptonbutterfly.l4j.util.ObservableLangDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public final class SearchGui extends ObservableLangDialog<BL, Void, SearchKeyData>
{
	static final String	buttonSearch	= "Search.button.search";
	static final String	buttonCancel	= "Search.button.cancel";
	
	final KeyTab keyTab;
	
	public SearchGui(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<Void>> closeListener,
		Localizer localizer,
		SearchKeyData data)
	{
		super(owner, modality, closeListener, localizer, data);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		reg("Search.title", this::setTitle);
		setMinimumSize(new Dimension(300, 175));
		Globals.windowStates.searchWindow.setBounds(this);
		
		final var tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		setContentPane(tabbedPane);
		
		keyTab = new KeyTab(this, businessLogic, this::reg);
		tabbedPane.addTab(null, keyTab.panel);
		reg("Search.Tab.Key", s -> tabbedPane.setTitleAt(0, s));
		tabbedPane.setEnabledAt(0, true);
	}
	
	@Override
	protected BL createBusinessLogic(SearchKeyData data)
	{
		return new BL(this, data);
	}
}