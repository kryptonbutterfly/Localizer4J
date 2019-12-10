package de.tinycodecrank.l4j.ui.main.parts;

import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import de.tinycodecrank.l4j.data.index.ProjectStringsIndex;
import de.tinycodecrank.l4j.data.index.ProjectStringsIndex.Index;

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
		if(indexes != null)
		{
			for(Index index : indexes.getOccurences(key))
			{
				String fileName = index.fileName;
				String lineText = index.lineContent;
				TableRowContent row = new TableRowContent(fileName, lineText, index.line);
				content.add(row);
			}
		}
		this.content.sort((a, b)->{return a.compareTo(b);});
		this.fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void setHeaderTitle(String title, int column)
	{
		this.columnIdentifiers.set(column, title);
		this.fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW, TableModelEvent.HEADER_ROW, column, TableModelEvent.UPDATE));
	}
	
	@Override
	public int getRowCount()
	{
		if(content != null)
		{
			return this.content.size();
		}
		else
		{
			return 0;
		}
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
		switch(column)
		{
			case 0:
				return trc.lineNumber;
			case 1:
				return trc.fileName;
			case 2:
				return trc.lineText;
			default:
				return null;
		}
	}
	
	private class TableRowContent implements Comparable<TableRowContent>
	{
		private String fileName;
		private String lineText;
		private int lineNumber;
		
		private TableRowContent(String fileName, String lineText, int lineNumber)
		{
			this.fileName = fileName;
			this.lineText = lineText;
			this.lineNumber = lineNumber;
		}

		@Override
		public int compareTo(TableRowContent o)
		{
			int result = this.fileName.compareTo(o.fileName);
			if(result != 0)
			{
				return result;
			}
			else
			{
				result = this.lineNumber - o.lineNumber;
				if(result == 0)
				{
					return 0;
				}
				else if(result > 0)
				{
					return 1;
				}
				else
				{
					return -1;
				}
			}
		}
	}
}