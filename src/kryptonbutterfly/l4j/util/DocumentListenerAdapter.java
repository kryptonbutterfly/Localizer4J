package kryptonbutterfly.l4j.util;

import java.util.function.Consumer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DocumentListenerAdapter implements DocumentListener
{
	private final Consumer<DocumentEvent>	insertUpdate;
	private final Consumer<DocumentEvent>	removeUpdate;
	private final Consumer<DocumentEvent>	changedUpdate;
	
	public DocumentListenerAdapter(
		Consumer<DocumentEvent> insertUpdate,
		Consumer<DocumentEvent> removeUpdate,
		Consumer<DocumentEvent> changedUpdate)
	{
		this.insertUpdate	= insertUpdate;
		this.removeUpdate	= removeUpdate;
		this.changedUpdate	= changedUpdate;
	}
	
	@Override
	public void insertUpdate(DocumentEvent e)
	{
		insertUpdate.accept(e);
	}
	
	@Override
	public void removeUpdate(DocumentEvent e)
	{
		removeUpdate.accept(e);
	}
	
	@Override
	public void changedUpdate(DocumentEvent e)
	{
		changedUpdate.accept(e);
	}
}