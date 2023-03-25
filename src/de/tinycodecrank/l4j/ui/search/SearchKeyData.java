package de.tinycodecrank.l4j.ui.search;

import java.util.function.Consumer;

import javax.swing.JTable;

public record SearchKeyData(JTable table, Consumer<String> setSelection)
{}