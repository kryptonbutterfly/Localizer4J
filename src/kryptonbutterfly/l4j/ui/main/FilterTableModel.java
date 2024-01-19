package kryptonbutterfly.l4j.ui.main;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import javax.swing.table.AbstractTableModel;

import kryptonbutterfly.l4j.data.gui.Translatable;
import kryptonbutterfly.l4j.data.gui.Translatable.TranslationState;
import kryptonbutterfly.l4j.data.gui.Translation;
import kryptonbutterfly.l4j.data.gui.Untranslated;
import kryptonbutterfly.l4j.data.index.ProjectStringsIndex;
import kryptonbutterfly.l4j.data.index.ProjectStringsIndex.Index;
import kryptonbutterfly.l4j.data.persistence.Language;
import kryptonbutterfly.math.utils.limit.LimitInt;
import kryptonbutterfly.math.utils.limit.OutOfBoundsException;

public class FilterTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Language> available = new HashMap<>();
	
	private Language language = null;
	
	private ProjectStringsIndex	sources		= new ProjectStringsIndex();
	private ProjectStringsIndex	miscContent	= new ProjectStringsIndex();
	
	private Predicate<Translatable> filter;
	
	private ArrayList<Translation>	translated			= new ArrayList<>();
	private ArrayList<Untranslated>	missing				= new ArrayList<>();
	private ArrayList<Untranslated>	untranslated		= new ArrayList<>();
	private ArrayList<Untranslated>	untranslatedMisc	= new ArrayList<>();
	
	private String[] header;
	
	public FilterTableModel(String[] header, Predicate<Translatable> filter)
	{
		this.header	= Arrays.copyOf(header, header.length);
		this.filter	= filter;
	}
	
	public void setHeaderTitle(String title, int index)
	{
		header[index] = title;
		recalculate();
	}
	
	public void setContent(ProjectStringsIndex sources, ProjectStringsIndex misc, HashMap<String, Language> available)
	{
		this.sources		= sources;
		this.miscContent	= misc;
		this.available		= available;
		recalculate();
	}
	
	public void setLanguage(Language selected)
	{
		this.language = selected;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return switch (columnIndex)
		{
		case 1 -> Translatable.class;
		case 2 -> String.class;
		default -> String.class;
		};
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return switch (rowIndex)
		{
		case (1) -> LimitInt.inRange(0, rowIndex, translated.size() + missing.size() - 1);
		default -> false;
		};
	}
	
	@Override
	public int getRowCount()
	{
		return translated.size() + missing.size() + untranslated.size() + untranslatedMisc.size();
	}
	
	@Override
	public int getColumnCount()
	{
		return header.length;
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int column)
	{
		if (isCellEditable(row, column))
		{
			final String	template	= "Changing Cell [%d,%d] is not supported!";
			String			msg			= String.format(template, column, row);
			throw new OutOfBoundsException(msg);
		}
		if (aValue == null || !(aValue instanceof String value))
			return;
		
		final String key = value.trim();
		if (key.isEmpty())
			return;
		
		if (row < translated.size())
		{
			Translation translation = translated.get(row);
			if (!translation.getKey().equals(key))
			{
				available.values()
					.stream()
					.filter(l -> l != language)
					.forEach(l ->
					{
						Translation original = null;
						Translation target	= null;
						for (Translation tmp : l.translations)
						{
							if (original == null && tmp.getKey().equals(translation.getKey()))
								original = tmp;
							if (target == null && tmp.getKey().equals(key))
								target = tmp;
							if (original != null && target != null)
								break;
						}
						if (target != null)
							l.translations.remove(original);
						else
							original.setKey(key);
					});
				translation.setKey(key);
			}
		}
		else
		{
			row -= translated.size();
			Translatable translatable = missing.get(row);
			if (!translatable.getKey().equals(key))
			{
				available.values()
					.stream()
					.forEach(l ->
					{
						Translation original = null;
						Translation target	= null;
						for (Translation tmp : l.translations)
						{
							if (original == null && tmp.getKey().equals(translatable.getKey()))
								original = tmp;
							if (target == null && tmp.getKey().equals(key))
								target = tmp;
							if (original != null && target != null)
								break;
						}
					});
			}
		}
		recalculate();
	}
	
	public Translatable getTranslatable(int row)
	{
		if (!LimitInt.inRange(0, row, getRowCount()))
			return null;
		
		if (row < translated.size())
			return translated.get(row);
		
		row -= translated.size();
		if (row < missing.size())
			return missing.get(row);
		
		row -= missing.size();
		if (row < untranslated.size())
			return untranslated.get(row);
		
		row -= untranslated.size();
		if (row < untranslatedMisc.size())
			return untranslatedMisc.get(row);
		
		return null;
	}
	
	@Override
	public Object getValueAt(int row, int column)
	{
		LimitInt.assertLimit(0, column, header.length, "column");
		Translatable translatable = getTranslatable(row);
		if (translatable == null)
			return null;
		
		return switch (column)
		{
		case 0 ->
		{
			if (sources == null || miscContent == null)
				yield "?";
			int	sourceOccurences	= sources.getOccurences(translatable.getKey()).size();
			int	miscOccurences		= miscContent.getOccurences(translatable.getKey()).size();
			
			if (sourceOccurences != 0)
				yield Integer.toString(sourceOccurences);
			yield Integer.toString(miscOccurences);
		}
		case 1 -> translatable;
		default -> null;
		};
	}
	
	public String getStringAt(int row, int column)
	{
		Object o = getValueAt(row, column);
		if (o == null)
			return null;
		
		if (column == 1)
			return ((Translatable) o).getKey();
		return (String) o;
	}
	
	@Override
	public String getColumnName(int column)
	{
		LimitInt.assertLimit(0, column, header.length - 1, "column");
		return header[column];
	}
	
	@SuppressWarnings("unlikely-arg-type")
	void recalculate()
	{
		translated.clear();
		missing.clear();
		untranslated.clear();
		untranslatedMisc.clear();
		if (language != null)
		{
			language.translations.stream().filter(filter).forEach(translated::add);
			
			for (Language lang : available.values())
			{
				lang.translations.stream()
					.filter(filter)
					.filter(t -> !language.translations.contains(t))
					.forEach(t -> missing.add(new Untranslated(t.getKey(), TranslationState.MISSING_TRANSLATABLE)));
			}
			
			if (sources != null)
			{
				sources.stream()
					.map(Entry::getKey)
					.map(k -> new Untranslated(k, TranslationState.SOURCE_TRANSLATABLE))
					.filter(filter)
					.filter(t -> !translated.contains(t))
					.filter(t -> !missing.contains(t))
					.forEach(untranslated::add);
			}
			
			if (miscContent != null)
			{
				miscContent.stream()
					.map(Entry::getKey)
					.map(k -> new Untranslated(k, TranslationState.MISC_TRANSLATABLE))
					.filter(filter)
					.filter(t -> !translated.contains(t))
					.filter(t -> !missing.contains(t))
					.filter(t -> sources == null || !sources.containsKey(t.getKey()))
					.forEach(untranslatedMisc::add);
			}
			
			adjustTranslationStates();
			
			translated.sort(Translatable::compareTo);
			missing.sort(Translatable::compareTo);
			untranslated.sort(Translatable::compareTo);
			untranslatedMisc.sort(Translatable::compareTo);
		}
		this.fireTableDataChanged();
	}
	
	private void adjustTranslationStates()
	{
		for (Translation t : translated)
		{
			Set<Index>	sourceOcc	= sources.getOccurences(t.getKey());
			Set<Index>	miscOcc		= miscContent.getOccurences(t.getKey());
			if (sourceOcc != null && sourceOcc.size() > 0 || miscOcc != null && miscOcc.size() > 0)
			{
				if (TranslationState.TRANSLATED_UNUSED == t.getTranslationState())
					t.setTranslationState(TranslationState.TRANSLATED);
			}
			else
			{
				if (TranslationState.TRANSLATED == t.getTranslationState())
					t.setTranslationState(TranslationState.TRANSLATED_UNUSED);
			}
		}
	}
	
	public void removeRow(String key)
	{
		for (int row : range(translated.size()))
			if (translated.get(row).getKey().equals(key))
			{
				removeRow(row);
				return;
			}
		
		for (int row : range(missing.size()))
			if (missing.get(row).getKey().equals(key))
			{
				removeRow(row + translated.size());
				return;
			}
	}
	
	public void removeRow(int row)
	{
		if (!LimitInt.inRange(0, row, translated.size() + missing.size() - 1))
			return;
		
		final Translatable target;
		if (row < translated.size())
			target = translated.get(row);
		else
			target = missing.get(row - translated.size());
		
		for (Language lang : available.values())
			lang.translations.remove(target);
		
		recalculate();
	}
	
	public int find(String key)
	{
		for (int i : range(translated.size()))
			if (translated.get(i).getKey().equals(key))
				return i;
			
		for (int i : range(missing.size()))
			if (missing.get(i).getKey().equals(key))
				return translated.size() + i;
			
		for (int i : range(untranslated.size()))
			if (untranslated.get(i).getKey().equals(key))
				return translated.size() + missing.size() + i;
			
		for (int i : range(untranslatedMisc.size()))
			if (untranslatedMisc.get(i).getKey().equals(key))
				return translated.size() + missing.size() + untranslated.size() + i;
			
		return -1;
	}
}