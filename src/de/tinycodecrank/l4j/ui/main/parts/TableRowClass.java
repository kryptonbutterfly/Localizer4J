package de.tinycodecrank.l4j.ui.main.parts;

import java.util.Objects;

class TableRowClass implements Comparable<TableRowClass>
{
	int		line;
	String	className;
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof TableRowClass other))
			return false;
		return this.compareTo(other) == 0;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(className, line);
	}
	
	@Override
	public int compareTo(TableRowClass o)
	{
		int result = this.className.compareTo(o.className);
		if (result != 0)
		{
			return result;
		}
		else
		{
			result = this.line - o.line;
			if (result == 0)
			{
				return 0;
			}
			else if (result > 0)
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