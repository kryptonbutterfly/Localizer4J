package de.tinycodecrank.l4j.ui.main.parts;

import java.util.ArrayList;
import java.util.Objects;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import de.tinycodecrank.l4j.data.index.ProjectStringsIndex;
import de.tinycodecrank.l4j.data.index.ProjectStringsIndex.Index;
import de.tinycodecrank.math.utils.limit.LimitInt;

public class TableModelMisc extends DefaultTableModel
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<TableRowContent> content = new ArrayList<>();
	
	public TableModelMisc(String[] header)
	{
		super(header, 0);
	}
	
	public void clearContent()
	{
		this.content = new ArrayList<>();
		this.fireTableDataChanged();
	}
	
	public void setContent(ProjectStringsIndex indexes, String key, String path)
	{
		this.content = new ArrayList<>();
		if (indexes != null)
			for (Index index : indexes.getOccurences(key))
			{
				String			fileName	= index.fileName;
				String			lineText	= index.lineContent;
				TableRowContent	row			= new TableRowContent(fileName, lineText, index.line);
				content.add(row);
			}
		
		this.content.sort(TableRowContent::compareTo);
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
		TableRowContent trc = content.get(row);
		return switch (column)
		{
			case 0 -> trc.lineNumber;
			case 1 -> trc.fileName;
			case 2 -> trc.lineText;
			default -> null;
		};
	}
	
	private static class TableRowContent implements Comparable<TableRowContent>
	{
		private String	fileName;
		private String	lineText;
		private int		lineNumber;
		
		private TableRowContent(String fileName, String lineText, int lineNumber)
		{
			this.fileName	= fileName;
			this.lineText	= lineText;
			this.lineNumber	= lineNumber;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == null)
				return false;
			if (obj == this)
				return true;
			if (!(obj instanceof TableRowContent other))
				return false;
			return compareTo(other) == 0;
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(fileName, lineNumber);
		}
		
		@Override
		public int compareTo(TableRowContent o)
		{
			int result = this.fileName.compareTo(o.fileName);
			if (result != 0)
				return result;
			return LimitInt.clamp(-1, this.lineNumber - o.lineNumber, 1);
		}
	}
}