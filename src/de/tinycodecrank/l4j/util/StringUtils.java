package de.tinycodecrank.l4j.util;

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
}