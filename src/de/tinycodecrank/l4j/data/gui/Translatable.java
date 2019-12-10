package de.tinycodecrank.l4j.data.gui;

import java.util.Objects;

import org.apache.commons.text.similarity.LevenshteinDistance;

public abstract class Translatable implements Comparable<Translatable>
{
	private static final LevenshteinDistance distance = new LevenshteinDistance(5);
	
	private String key;
	private TranslationState state;
	
	Translatable(TranslationState state, String key)
	{
		this.state = state;
		this.key = key;
	}

	@Override
	public int compareTo(Translatable o)
	{
		if(o == null)
		{
			return 1;
		}
		else
		{
			int stateDiff = this.state.ordinal() - o.getTranslationState().ordinal();
			if(stateDiff == 0)
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
		MISC_TRANSLATABLE,
		SOURCE_TRANSLATABLE,
		TRANSLATED,
		TRANSLATED_UNUSED,
		MISSING_TRANSLATABLE;
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
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if(obj instanceof Translatable)
		{
			Translatable other = (Translatable) obj;
			return Objects.equals(key, other.key);
		}
		else
		{
			return false;
		}
	}
}