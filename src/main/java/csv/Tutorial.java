package main.java.csv;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class Tutorial
{
	public static void main(String[] args)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		File file = new File("C:\\temp\\out.csv");
		
		//You can export beans that have fields or methods annotated with CsvExportValue
		ReflectionCsvExporter<Person> exporter = new ReflectionCsvExporter<>(Person.class, file);
		Person data = getMockData();
		exporter.emit(data);
		exporter.close();

		// The exporter will recognize the header of given csv-File and export the data accordingly 
		ReflectionCsvExporter<Person> exporter2 = new ReflectionCsvExporter<>(Person.class, file);
		List<Person> list = getMockData(1);
		exporter2.emit(list); // You can even export whole lists of beans
		exporter2.close();
	}

	private static Person getMockData()
	{
		return new Person("Harald", "Bach", 51);
	}

	private static List<Person> getMockData(int param)
	{
		ArrayList<Person> list = new ArrayList<Person>();
		switch (param % 2)
		{
			case 1:
				list.add(new Person("Max", "Mustermann", 23));
				list.add(new Person("Maria", "Mustermann", 21));
				list.add(new Person("Felix", "Herbert", 17));
				break;
			case 2:
				list.add(new Person("Hubert", "Schaub", 65));
				list.add(new Person("Tim", "Vettel", 21));
				list.add(new Person("Marc", "Motzu", 42));
				break;
		}
		return list;
	}

	public static class Person
	{
		private static int	idCounter	= 0;
		private int			id;
		private String		firstname;
		private String		lastname;

		@CsvExportValue(orderIdx = 3)
		private int			age;

		public Person(String firstname, String lastname, int age)
		{
			this.id = idCounter;
			this.firstname = firstname;
			this.lastname = lastname;
			this.age = age;

			idCounter++;
		}

		@CsvExportValue(orderIdx = 1)
		public String getId()
		{
			return String.format("%03d", this.id);
		}

		@CsvExportValue(orderIdx = 2)
		public String getName()
		{
			return firstname + " " + lastname;
		}
	}
}
