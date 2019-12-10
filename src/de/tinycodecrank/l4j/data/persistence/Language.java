package de.tinycodecrank.l4j.data.persistence;

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;

import de.tinycodecrank.l4j.data.gui.Translation;

public class Language
{
	private LanguageLoader loader;
	
	private String					name;
	public SortedSet<Translation>	translations	= new TreeSet<>();
	public int						version			= 1;
	private boolean					dirty			= false;
	
	public Language(File file, LanguageLoader loader)
	{
		String fileName = file.getName();
		this.name	= fileName.substring(0, fileName.lastIndexOf("."));
		this.loader	= loader;
		this.load(file);
	}
	
	public void setLoader(LanguageLoader loader)
	{
		this.loader = loader;
	}
	
	public Language(String name, LanguageLoader loader)
	{
		this.name	= name;
		this.loader	= loader;
	}
	
	private void load(File file)
	{
		this.loader.load(this, file);
	}
	
	public void save(File folder)
	{
		if (dirty)
		{
			dirty = false;
			version++;
		}
		this.loader.save(this, folder);
	}
	
	public void delete(File langFolder)
	{
		this.loader.delete(this, langFolder);
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public int hashCode()
	{
		final int	prime	= 31;
		int			result	= 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Language other = (Language) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public void dirty(boolean dirty)
	{
		this.dirty = dirty;
	}
	
	public boolean dirty()
	{
		return this.dirty;
	}
}