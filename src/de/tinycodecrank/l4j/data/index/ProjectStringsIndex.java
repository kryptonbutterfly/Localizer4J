package de.tinycodecrank.l4j.data.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import de.tinycodecrank.collections.data.Tuple;

public class ProjectStringsIndex implements Iterable<Tuple<String, Integer>>
{
	private final HashMap<String, Set<Index>> indexes = new HashMap<>();
	
	public void put(String string , String fileName, String lineContent, int line)
	{
		Set<Index> set = indexes.get(string);
		if(set == null)
		{
			set = new HashSet<>();
			indexes.put(string, set);
		}
		set.add(new Index(fileName, lineContent, line));
	}
	
	public Set<Index> getOccurences(String string)
	{
		Set<Index> set = indexes.get(string);
		if(set != null)
		{
			return Collections.unmodifiableSet(set);
		}
		else
		{
			return Collections.unmodifiableSet(new HashSet<Index>());
		}
	}
	
	public boolean containsKey(String string)
	{
		return indexes.containsKey(string);
	}
	
	public boolean contains(String string, String fileName, int line)
	{
		Set<Index> set = indexes.get(string);
		if(set != null)
		{
			Index target = new Index(fileName, null, line);
			return set.contains(target);
		}
		else
		{
			return false;
		}
	}
	
	public static class Index
	{
		public final String fileName;
		public final String lineContent;
		public final int line;
		
		private Index(String fileName, String lineContent, int line)
		{
			this.fileName = fileName;
			this.lineContent = lineContent;
			this.line = line;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
			result = prime * result + line;
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Index other = (Index) obj;
			if (fileName == null)
			{
				if (other.fileName != null) return false;
			}
			else if (!fileName.equals(other.fileName)) return false;
			if (line != other.line) return false;
			return true;
		}
	}

	@Override
	public Iterator<Tuple<String, Integer>> iterator()
	{
		return new Iterator<Tuple<String,Integer>>()
		{
			final Iterator<Entry<String, Set<Index>>> it = indexes.entrySet().iterator();
			
			@Override
			public boolean hasNext()
			{
				return it.hasNext();
			}

			@Override
			public Tuple<String, Integer> next()
			{
				Entry<String, Set<Index>> entry = it.next();
				return new Tuple<>(entry.getKey(), entry.getValue().size());
			}
		};
	}
	
	public Stream<Entry<String, Set<Index>>> stream()
	{
		return indexes.entrySet().stream();
	}

	public Stream<Entry<String, Set<Index>>> parallelStream()
	{
		return indexes.entrySet().parallelStream();
	}
	
}