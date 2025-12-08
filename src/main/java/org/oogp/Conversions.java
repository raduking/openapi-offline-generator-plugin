package org.oogp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.morphix.convert.MapConversions;
import org.morphix.reflection.Fields;
import org.morphix.reflection.InstanceCreator;

/**
 * Utility class for class-related operations including class discovery, loading, and conversion.
 * <p>
 * The class uses the current thread's context class loader for class loading operations and relies on the
 * 'project.build.outputDirectory' system property to locate compiled classes.
 *
 * @author Radu Sebastian LAZIN
 */
public class Conversions {

	/**
	 * Hide constructor.
	 */
	private Conversions() {
		// empty
	}

	/**
	 * Converts a string array to an instance of the specified class by mapping array elements to the class fields in
	 * declaration order.
	 * <ul>
	 * <li>The method maps array elements to fields based on their declaration order.</li>
	 * <li>If the array has fewer elements than fields, only the corresponding fields are set.</li>
	 * <li>If the array has more elements than fields, extra elements are ignored.</li>
	 * </ul>
	 *
	 * @param <T> the type of the target class
	 *
	 * @param args the string array containing values to be assigned to fields
	 * @param cls the target class to create an instance of
	 * @return a new instance of the specified class with fields populated from the string array
	 */
	public static <T> T convertFromStringArray(final String[] args, final Class<T> cls) {
		List<Field> fields = Fields.getAllDeclared(cls);
		Map<String, String> fieldValueMap = HashMap.newHashMap(args.length);
		for (int i = 0; i < Math.min(args.length, fields.size()); ++i) {
			fieldValueMap.put(fields.get(i).getName(), args[i]);
		}
		return MapConversions.convertFromMap(fieldValueMap, () -> InstanceCreator.getInstance().newInstance(cls));
	}

}
