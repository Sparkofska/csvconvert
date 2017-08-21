package main.java;

/**
 * TODO write doc
 * 
 * @author jonas
 *
 */
public class Rule
{
	private String originColumn;
	private String destinationColumn;
	private String defaultValue;

	public String getOriginColumn()
	{
		return originColumn;
	}

	public void setOriginColumn(String originColumn)
	{
		this.originColumn = originColumn;
	}

	public String getDestinationColumn()
	{
		return destinationColumn;
	}

	public void setDestinationColumn(String destinationColumn)
	{
		this.destinationColumn = destinationColumn;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString()
	{
		return "Rule [originColumn=" + originColumn + ", destinationColumn=" + destinationColumn + ", defaultValue="
				+ defaultValue + "]";
	}

}
