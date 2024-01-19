package kryptonbutterfly.l4j.data.gui;

public class Translation extends Translatable
{
	private String translation;
	
	public Translation(String key, String translation)
	{
		super(TranslationState.TRANSLATED, key);
		this.translation = translation;
	}
	
	public Translation(String key)
	{
		this(key, null);
	}

	public String getTranslation()
	{
		return translation;
	}

	public void setTranslation(String translation)
	{
		this.translation = translation;
	}
}