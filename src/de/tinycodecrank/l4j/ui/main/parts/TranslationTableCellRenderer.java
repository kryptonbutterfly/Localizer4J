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
	private static final Color	light_Red		= new Color(255, 191, 191);
	private static final Color	light_Gray		= new Color(191, 191, 191);
	private static final Color	light_Green		= new Color(191, 255, 191);
	private static final Color	light_Yellow	= new Color(255, 255, 191);
	
	private static final Color	dark_Red	= new Color(64, 0, 0);
	private static final Color	dark_Gray	= new Color(96, 96, 96);
	private static final Color	dark_Green	= new Color(0, 64, 0);
	private static final Color	dark_Yellow	= new Color(64, 64, 0);
	
	private static final Color	font_Light	= new Color(223, 223, 223);
	private static final Color	font_Dark	= new Color(32, 32, 32);
	
	private static final String	I18N_MISSING		= "Main.table.tooltip.status.missing";
	private static final String	I18N_LOCALIZABLE	= "Main.table.tooltip.status.localizable";
	private static final String	I18N_TRANSLATED		= "Main.table.tooltip.status.translated";
	private static final String	I18N_UNUSED			= "Main.table.tooltip.status.unused";
	
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
		if (table != null && value instanceof Translatable)
		{
			final Translatable	translatable	= (Translatable) value;
			Color				bgColor			= table.getBackground();
			super.setForeground(ColorUtils.isBright(table.getForeground()) ? font_Light : font_Dark);
			switch (translatable.getTranslationState())
			{
				case TRANSLATED:
					bgColor = ColorUtils.isBright(table.getBackground()) ? light_Green : dark_Green;
					super.setToolTipText(l10n.localize(I18N_TRANSLATED));
					break;
				case TRANSLATED_UNUSED:
					bgColor = ColorUtils.isBright(table.getBackground()) ? light_Yellow : dark_Yellow;
					super.setToolTipText(l10n.localize(I18N_UNUSED));
					break;
				case MISSING_TRANSLATABLE:
					bgColor = ColorUtils.isBright(table.getBackground()) ? light_Red : dark_Red;
					super.setToolTipText(l10n.localize(I18N_MISSING));
					break;
				case SOURCE_TRANSLATABLE:
					super.setToolTipText(l10n.localize(I18N_LOCALIZABLE));
					break;
				case MISC_TRANSLATABLE:
					bgColor = ColorUtils.isBright(table.getBackground()) ? light_Gray : dark_Gray;
					super.setToolTipText(l10n.localize(I18N_LOCALIZABLE));
					break;
				default:
					super.setToolTipText("");
					break;
			}
			if (bgColor != null)
			{
				super.setBackground(bgColor);
			}
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}