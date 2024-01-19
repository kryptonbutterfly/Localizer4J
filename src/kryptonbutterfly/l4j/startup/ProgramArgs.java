package kryptonbutterfly.l4j.startup;

import kryptonbutterfly.args.AProgramArguments;
import kryptonbutterfly.args.Argument;

public class ProgramArgs extends AProgramArguments
{
	protected ProgramArgs(String[] args, String programInfo)
	{
		super(args, programInfo);
	}
	
	@Override
	protected void addAllParser()
	{}
	
	@Argument(info = "the config file of the project to load", name = "pF")
	public String[] projectFile;
	
	@Argument(info = "creates a shortcut unless set to true or pF is set", name = "i")
	public boolean initialized;
}