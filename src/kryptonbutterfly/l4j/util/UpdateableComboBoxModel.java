package kryptonbutterfly.l4j.util;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public class UpdateableComboBoxModel<E> extends DefaultComboBoxModel<E>
{
	public UpdateableComboBoxModel()
	{
		super();
	}
	
	public UpdateableComboBoxModel(E[] items)
	{
		super(items);
	}
	
	public UpdateableComboBoxModel(Vector<E> v)
	{
		super(v);
	}
	
	public void fireChange()
	{
		fireContentsChanged(this, -1, -1);
	}
}