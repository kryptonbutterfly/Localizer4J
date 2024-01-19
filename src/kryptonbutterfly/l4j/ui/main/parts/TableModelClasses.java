package kryptonbutterfly.l4j.ui.main.parts;

import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import kryptonbutterfly.l4j.data.index.ProjectStringsIndex;
import kryptonbutterfly.l4j.data.index.ProjectStringsIndex.Index;

public class TableModelClasses extends DefaultTableModel
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<TableRowClass> content = new ArrayList<>();
	
	public TableModelClasses(String[] header)
	{
		super(header, 0);
	}
	
	public void clearContent()
	{
		this.content = new ArrayList<>();
		this.fireTableDataChanged();
	}
	
	public void setContent(ProjectStringsIndex classes, String key, String path)
	{
		this.content = new ArrayList<>();
		if (classes != null)
			for (Index ind : classes.getOccurences(key))
			{
				TableRowClass trc = new TableRowClass();
				trc.className	= ind.fileName;
				trc.line		= ind.line;
				content.add(trc);
			}
		this.content.sort(TableRowClass::compareTo);
		this.fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void setHeaderTitle(String title, int column)
	{
		this.columnIdentifiers.set(column, title);
		this.fireTableChanged(
			new TableModelEvent(
				this,
				TableModelEvent.HEADER_ROW,
				TableModelEvent.HEADER_ROW,
				column,
				TableModelEvent.UPDATE));
	}
	
	@Override
	public int getRowCount()
	{
		if (content == null)
			return 0;
		return this.content.size();
	}
	
	@Override
	public Class<String> getColumnClass(int columnIndex)
	{
		return String.class;
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
	
	@Override
	public Object getValueAt(int row, int column)
	{
		TableRowClass trc = content.get(row);
		return switch (column)
		{
			case 0 -> Integer.toString(trc.line);
			case 1 -> trc.className;
			default -> null;
		};
	}
}