package kryptonbutterfly.l4j.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;

import kryptonbutterfly.i18n.Localization;
import kryptonbutterfly.i18n.Localizer;

public class LangManager implements AutoCloseable
{
	private final HashSet<Localization>	localizations	= new HashSet<>();
	public final Localizer				localizer;
	
	public LangManager(Localizer localizer)
	{
		this.localizer = localizer;
	}
	
	@SafeVarargs
	public final void reg(String key, Consumer<String>... listener)
	{
		final String translation = localizer.localize(key);
		if (!translation.isEmpty())
			localizations.add(localizer.get(key, listener));
		else
			Arrays.stream(listener).forEach(l -> l.accept(null));
	}
	
	@SafeVarargs
	public final Localization get(String key, Consumer<String>... listener)
	{
		final var translation = localizer.get(key, listener);
		localizations.add(translation);
		return translation;
	}
	
	@Override
	public void close()
	{
		localizations.forEach(Localization::close);
	}
}