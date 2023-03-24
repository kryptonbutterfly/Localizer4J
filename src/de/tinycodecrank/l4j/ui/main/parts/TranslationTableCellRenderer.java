package de.tinycodecrank.l4j.ui.main.parts;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.tinycodecrank.i18n.Localizer;
import de.tinycodecrank.l4j.data.gui.Translatable;
import de.tinycodecrank.l4j.util.ColorUtils;

@SuppressWarnings("serial")
public class TranslationTableCellRenderer extends DefaultTableCellRenderer
{
	private static final Color	font_Light	= new Color(223, 223, 223);
	private static final Color	font_Dark	= new Color(32, 32, 32);
	
	private final Localizer l10n;
	
	public TranslationTableCellRenderer(Localizer l10n)
	{
		this.l10n = l10n;
	}
	
	@Override
	public Component getTableCellRendererComponent(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column)
	{
		if (table != null && value instanceof Translatable translatable)
		{
			Color bgColor = table.getBackground();
			super.setForeground(ColorUtils.isBright(table.getForeground()) ? font_Light : font_Dark);
			final var translationState = translatable.getTranslationState();
			super.setToolTipText(l10n.localize(translationState.tooltip));
			super.setBackground(translationState.bgColor(bgColor));
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}