package de.tinycodecrank.l4j.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

import de.tinycodecrank.collections.ArrayUtils;

@FunctionalInterface
public interface KeyEventListener extends Predicate<KeyEvent>
{
	public static KeyListener createUnrestricted(KeyEventType type, KeyEventListener listener)
	{
		return create(type, e ->
		{
			if (!e.isConsumed())
				if (listener.test(e))
					e.consume();
		});
	}
	
	public static KeyListener create(KeyEventType type, KeyEventListener listener, int... keys)
	{
		Arrays.sort(keys);
		return create(type, e ->
		{
			if (!e.isConsumed())
				if (ArrayUtils.find(keys, e.getExtendedKeyCode()) != -1)
					if (listener.test(e))
						e.consume();
		});
	}
	
	public static KeyListener create(KeyEventType type, int mask, KeyEventListener listener, int... keys)
	{
		Arrays.sort(keys);
		return create(type, e ->
		{
			if (!e.isConsumed())
				if (mask == e.getModifiersEx())
					if (ArrayUtils.find(keys, e.getExtendedKeyCode()) != -1)
						if (listener.test(e))
							e.consume();
		});
	}
	
	private static KeyListener create(KeyEventType type, Consumer<KeyEvent> listener)
	{
		return switch (type)
		{
			case TYPED -> new KeyAdapter()
			{
				@Override
				public void keyTyped(KeyEvent e)
				{
					listener.accept(e);
				}
			};
			case PRESSED -> new KeyAdapter()
			{
				@Override
				public void keyPressed(KeyEvent e)
				{
					listener.accept(e);
				}
			};
			case RELEASED -> new KeyAdapter()
			{
				@Override
				public void keyReleased(KeyEvent e)
				{
					listener.accept(e);
				}
			};
		};
	}
}