package kryptonbutterfly.l4j.ui.main.parts;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import kryptonbutterfly.l4j.util.ColorUtils;

public class CountTableCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	private static final Color	light_Yellow	= new Color(255, 255, 191);
	private static final Color	light_Red		= new Color(255, 191, 191);
	
	private static final Color	dark_Yellow	= new Color(64, 64, 0);
	private static final Color	dark_Red	= new Color(64, 0, 0);
	
	public CountTableCellRenderer()
	{}
	
	@Override
	public Component getTableCellRendererComponent(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column)
	{
		if (table != null && value instanceof String str)
			super.setBackground(switch (str)
			{
				case "?" -> ColorUtils.isBright(table.getBackground()) ? light_Yellow : dark_Yellow;
				case "0" -> ColorUtils.isBright(table.getBackground()) ? light_Red : dark_Red;
				default -> null;
			});
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}