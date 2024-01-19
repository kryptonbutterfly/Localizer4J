package kryptonbutterfly.l4j.prefs;

import java.util.LinkedList;

import kryptonbutterfly.xmlConfig4J.annotations.Value;

public class History
{
	@Value
	public LinkedList<String> recent = new LinkedList<>();
	
	@Value
	public int maxLength = 7;
}