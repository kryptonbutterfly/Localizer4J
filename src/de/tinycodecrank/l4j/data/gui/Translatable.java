package de.tinycodecrank.l4j.data.gui;

import java.awt.Color;
import java.util.Objects;

import org.apache.commons.text.similarity.LevenshteinDistance;

import de.tinycodecrank.l4j.util.ColorUtils;
import de.tinycodecrank.l4j.util.Constants;

public abstract class Translatable implements Comparable<Translatable>, Constants
{
	private static final LevenshteinDistance distance = new LevenshteinDistance(5);
	
	private String				key;
	private TranslationState	state;
	
	Translatable(TranslationState state, String key)
	{
		this.state	= state;
		this.key	= key;
	}
	
	@Override
	public int compareTo(Translatable o)
	{
		if (o == null)
		{
			return 1;
		}
		else
		{
			int stateDiff = this.state.ordinal() - o.getTranslationState().ordinal();
			if (stateDiff == 0)
			{
				return this.key.compareTo(o.getKey());
			}
			else
			{
				return stateDiff;
			}
		}
	}
	
	public int getDistance(String key)
	{
		Objects.requireNonNull(key);
		return distance.apply(key, this.key);
	}
	
	public final String getKey()
	{
		return key;
	}
	
	public final void setKey(String key)
	{
		this.key = key;
	}
	
	@Override
	public String toString()
	{
		return this.key;
	}
	
	public void setTranslationState(TranslationState state)
	{
		this.state = state;
	}
	
	public TranslationState getTranslationState()
	{
		return this.state;
	}
	
	public static enum TranslationState
	{
		MISC_TRANSLATABLE("Main.table.tooltip.status.localizable", light_Gray, dark_Gray),
		SOURCE_TRANSLATABLE("Main.table.tooltip.status.localizable", null, null),
		TRANSLATED("Main.table.tooltip.status.translated", light_Green, dark_Green),
		TRANSLATED_UNUSED("Main.table.tooltip.status.unused", light_Yellow, dark_Yellow),
		MISSING_TRANSLATABLE("Main.table.tooltip.status.missing", light_Red, dark_Red);
		
		public String	tooltip;
		private Color	bgBright;
		private Color	bgDark;
		
		private TranslationState(String tooltip, Color bgBright, Color bgDark)
		{
			this.tooltip	= tooltip;
			this.bgBright	= bgBright;
			this.bgDark		= bgDark;
		}
		
		public Color bgColor(Color bgOriginal)
		{
			if (bgBright == null)
				return bgOriginal;
			return ColorUtils.isBright(bgOriginal) ? bgBright : bgDark;
		}
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(key);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Translatable other))
			return false;
		return Objects.equals(key, other.key);
	}
}