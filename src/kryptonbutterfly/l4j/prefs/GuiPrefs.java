package kryptonbutterfly.l4j.prefs;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;

import kryptonbutterfly.math.utils.limit.LimitInt;
import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.util.swing.template.IWindowState;
import kryptonbutterfly.xmlConfig4J.annotations.Value;

public class GuiPrefs implements IWindowState
{
	private static final GuiPrefs DEFAULTS = new GuiPrefs();
	
	public GuiPrefs()
	{}
	
	public GuiPrefs(int posX, int posY, int width, int height, int state)
	{
		this.posX	= posX;
		this.posY	= posY;
		this.width	= width;
		this.height	= height;
		this.state	= state;
	}
	
	@Value
	public int posX = 100;
	
	@Value
	public int posY = 100;
	
	@Value
	public int width = 800;
	
	@Value
	public int height = 600;
	
	@Value
	public int screen = 0;
	
	@Value
	public int state = Frame.NORMAL;
	
	@Override
	public void setBounds(Component target)
	{
		final var	screens	= GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		final var	screen	= screens[LimitInt.clamp(0, this.screen, screens.length - 1)];
		final var	bounds	= screen.getDefaultConfiguration().getBounds();
		
		final int	x	= posX + bounds.x;
		final int	y	= posY + bounds.y;
		
		target.setBounds(x, y, width, height);
	}
	
	@Override
	public void setBoundsAndState(Frame target)
	{
		setBounds(target);
		target.setExtendedState(state);
	}
	
	@Override
	public void persistBounds(Component source)
	{
		persistBoundsAndState(source, Frame.NORMAL);
	}
	
	@Override
	public void persistBoundsAndState(Frame source)
	{
		state = source.getExtendedState();
		persistBoundsAndState(source, state);
	}
	
	private void persistBoundsAndState(Component source, int state)
	{
		if (GraphicsEnvironment.isHeadless())
			return;
		
		final var	DEFAULT_SCREEN_INDEX	= 0;
		final var	screens					= GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		var	currScreen		= source.getGraphicsConfiguration().getDevice();
		int	currScreenIndex	= findIndex(screens, currScreen, DEFAULT_SCREEN_INDEX);
		if (currScreenIndex == DEFAULT_SCREEN_INDEX && !findIndex(screens, this.screen).isPresent())
		{
			currScreenIndex	= Opt.of(this.screen)
				.filter(new LimitInt(0, screens.length - 1)::inRange)
				.get(() -> DEFAULT_SCREEN_INDEX);
			currScreen		= screens[currScreenIndex];
		}
		this.screen = currScreenIndex;
		
		final var screenBounds = currScreen.getDefaultConfiguration().getBounds();
		
		if (!source.isVisible())
			source.setVisible(true);
		final var loc = source.getLocationOnScreen();
		
		switch (state)
		{
		case Frame.NORMAL -> {
			this.posX	= loc.x - screenBounds.x;
			this.posY	= loc.y - screenBounds.y;
			this.width	= source.getWidth();
			this.height	= source.getHeight();
		}
		case Frame.ICONIFIED -> {} // Do nothing (preserve original size and position)
		case Frame.MAXIMIZED_BOTH -> {} // Do nothing (preserve original size and position)
		case Frame.MAXIMIZED_HORIZ -> {
			this.posY	= loc.y - screenBounds.y;
			this.height	= source.getHeight();
		}
		case Frame.MAXIMIZED_VERT -> {
			this.posX	= loc.x - screenBounds.x;
			this.width	= source.getWidth();
		}
		default -> {
			System.err
				.printf("Illegal Window state %s. Defaulting to NORMAL and resetting bounds to default.\n", state);
			state	= DEFAULTS.state;
			posX	= DEFAULTS.posX;
			posY	= DEFAULTS.posY;
			width	= DEFAULTS.width;
			height	= DEFAULTS.height;
			screen	= DEFAULTS.screen;
		}
		}
	}
	
	private static <E> Opt<Integer> findIndex(E[] array, E element)
	{
		for (int i : range(array.length))
			if (array[i] == element)
				return Opt.of(i);
		return Opt.empty();
	}
	
	private static <E> int findIndex(E[] array, E element, int fallback)
	{
		return findIndex(array, element).get(() -> fallback);
	}
}