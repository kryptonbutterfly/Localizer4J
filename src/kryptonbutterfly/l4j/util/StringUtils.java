package kryptonbutterfly.l4j.util;

import kryptonbutterfly.monads.opt.Opt;

public final class StringUtils
{
	public static boolean isNotEmpty(String str)
	{
		return !(str == null || str.isEmpty());
	}
	
	public static boolean isNotBlank(String str)
	{
		return !(str == null || str.isBlank());
	}
	
	public static Opt<Integer> camelDistance(String search, String target)
	{
		if (search.length() > target.length())
			return Opt.empty();
		
		final var	parts		= search.split("(?=\\p{Lu})");
		int			difference	= 0;
		for (final var part : parts)
		{
			final int index = target.indexOf(part);
			if (index == -1)
				return Opt.empty();
			if (index > 0)
				difference += index;
			target = target.substring(index + part.length());
		}
		return Opt.of(difference + target.length());
	}
	
	public static Opt<Integer> matchOffset(String search, String target)
	{
		if (search.length() > target.length())
			return Opt.empty();
		
		final int index = target.indexOf(search);
		if (index == -1)
			return Opt.empty();
		
		return Opt.of(target.substring(index + search.length()).length());
	}
}