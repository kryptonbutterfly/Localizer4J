module de.tinycodecrank.Localizer4J
{
	exports de.tinycodecrank.l4j.ui.main.parts;
	exports de.tinycodecrank.l4j.ui.lang;
	exports de.tinycodecrank.l4j.config;
	exports de.tinycodecrank.l4j.ui.settings;
	exports de.tinycodecrank.l4j.ui.project;
	exports de.tinycodecrank.l4j.prefs;
	exports de.tinycodecrank.l4j.data.index;
	exports de.tinycodecrank.l4j.misc;
	exports de.tinycodecrank.l4j.ui.main;
	exports de.tinycodecrank.l4j.startup;
	exports de.tinycodecrank.l4j.data.gui;
	exports de.tinycodecrank.l4j.util;
	exports de.tinycodecrank.l4j.data.persistence;
	
	opens de.tinycodecrank.l4j.config to de.tinycodecrank.xmlConfig4J;
	
	requires de.tinycodecrank.ArgsManager;
	requires de.tinycodecrank.Collections;
	requires de.tinycodecrank.IOUtils;
	requires transitive de.tinycodecrank.Localization;
	requires de.tinycodecrank.Monads;
	requires de.tinycodecrank.System;
	requires de.tinycodecrank.LocalizationVersionConfig;
	requires de.tinycodecrank.ReflectionUtils;
	requires de.tinycodecrank.SwingUtils;
	requires de.tinycodecrank.xmlConfig4J;
	requires transitive java.desktop;
	requires java.xml;
	requires de.tinycodecrank.mathUtils;
	requires de.tinycodecrank.JavaLexer;
	requires com.google.gson;
	requires de.tinycodecrank.Functional;
}