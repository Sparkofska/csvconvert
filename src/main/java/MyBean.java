package main.java;

public class MyBean
{
	private int age;
	private String name;

	public MyBean(int age, String name)
	{
		super();
		this.age = age;
		this.name = name;
	}

	public MyBean()
	{
		super();
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

}
