package kryptonbutterfly.l4j.ui.search;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.swing.JTable;

import kryptonbutterfly.monads.opt.Opt;

public record SearchKeyData(JTable table, BiConsumer<String, BiFunction<String, String, Opt<Integer>>> setSelection)
{}