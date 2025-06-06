package kryptonbutterfly.l4j.util;

import kryptonbutterfly.functions.throwing.RunnableThrowing;
import kryptonbutterfly.functions.throwing.SupplierThrowing;

public interface Sneaky
{
	public static <Ret, E extends Throwable> Ret sneaky(SupplierThrowing<Ret, E> supplier)
	{
		try
		{
			return supplier.get();
		}
		catch (Throwable e)
		{
			return sneaky(e);
		}
	}
	
	public static <E extends Throwable> void sneaky(RunnableThrowing<E> supplier)
	{
		try
		{
			supplier.run();
		}
		catch (Throwable e)
		{
			sneaky(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <Ret, E extends Throwable> Ret sneaky(Throwable e) throws E
	{
		throw (E) e;
	}
}