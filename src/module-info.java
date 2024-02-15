module kryptonbutterfly.Localizer4J
{
	exports kryptonbutterfly.l4j.ui.main.parts;
	exports kryptonbutterfly.l4j.ui.lang;
	exports kryptonbutterfly.l4j.config;
	exports kryptonbutterfly.l4j.ui.settings;
	exports kryptonbutterfly.l4j.ui.project;
	exports kryptonbutterfly.l4j.prefs;
	exports kryptonbutterfly.l4j.data.index;
	exports kryptonbutterfly.l4j.misc;
	exports kryptonbutterfly.l4j.ui.main;
	exports kryptonbutterfly.l4j.startup;
	exports kryptonbutterfly.l4j.data.gui;
	exports kryptonbutterfly.l4j.util;
	exports kryptonbutterfly.l4j.data.persistence;
	
	opens kryptonbutterfly.l4j.config to kryptonbutterfly.xmlConfig4J;
	
	requires com.google.gson;
	requires java.xml;
	requires transitive java.desktop;
	requires transitive kryptonbutterfly.Localization;
	requires kryptonbutterfly.ArgsManager;
	requires kryptonbutterfly.Collections;
	requires kryptonbutterfly.IOUtils;
	requires kryptonbutterfly.Monads;
	requires kryptonbutterfly.System;
	requires kryptonbutterfly.LocalizationVersionConfig;
	requires kryptonbutterfly.ReflectionUtils;
	requires transitive kryptonbutterfly.SwingUtils;
	requires kryptonbutterfly.xmlConfig4J;
	requires kryptonbutterfly.mathUtils;
	requires kryptonbutterfly.JavaLexer;
	requires kryptonbutterfly.Functional;
}