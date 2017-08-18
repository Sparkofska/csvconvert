package main.java.csv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.util.Comparator;

/**
 * 
 * @author Jonas Münch
 * @since 09.06.2017
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvExportValue
{
	int orderIdx() default 0;
}

class CsvExportValueComparator implements Comparator<AccessibleObject>
{
	@Override
	public int compare(AccessibleObject o1, AccessibleObject o2)
	{
		CsvExportValue anno1 = o1.getAnnotation(CsvExportValue.class);
		CsvExportValue anno2 = o2.getAnnotation(CsvExportValue.class);

		if (anno1 == null)
		{
			if (anno2 == null)
				return 0;
			else
				return 1;
		}
		if (anno2 == null)
			return -1;

		return anno1.orderIdx() - anno2.orderIdx();
	}
}