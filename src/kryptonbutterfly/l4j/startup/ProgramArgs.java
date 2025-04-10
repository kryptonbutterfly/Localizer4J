package kryptonbutterfly.l4j.startup;

import kryptonbutterfly.args.ArgsProperties;
import kryptonbutterfly.args.Argument;
import kryptonbutterfly.args.IArgs;
import kryptonbutterfly.l4j.util.Constants;

@ArgsProperties()
public class ProgramArgs implements IArgs
{
	@Argument(info = "the config file of the project to load", name = "pF")
	public String[] projectFile;
	
	@Argument(info = "creates a shortcut unless set to true or pF is set", name = "i")
	public boolean initialized = false;
	
	@Override
	public String programInfo()
	{
		return Constants.PROGRAM_INFO;
	}
}