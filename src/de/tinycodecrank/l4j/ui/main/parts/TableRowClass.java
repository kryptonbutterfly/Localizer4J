package de.tinycodecrank.l4j.ui.main.parts;

class TableRowClass implements Comparable<TableRowClass>
{
	int line;
	String className;
	
	
	@Override
	public int compareTo(TableRowClass o)
	{
		int result = this.className.compareTo(o.className);
		if(result != 0)
		{
			return result;
		}
		else
		{
			result = this.line - o.line;
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