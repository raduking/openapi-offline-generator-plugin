package org.oogp;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apiphany.lang.Strings;
import org.morphix.convert.MapConversions;
import org.morphix.lang.function.InstanceFunction;
import org.morphix.reflection.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Classes {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Classes.class);

	/**
	 * Hide constructor.
	 */
	private Classes() {
		// empty
	}

	public static Path detectDirectory() {
		String buildOutput = System.getProperty("project.build.outputDirectory");
		if (Strings.isNotEmpty(buildOutput)) {
			return Path.of(buildOutput);
		}
		LOGGER.error("Missing 'project.build.outputDirectory' property");
		throw new IllegalStateException("Could not detect project classes directory");
	}

	public static Set<Class<?>> findInPackage(final String basePackage, final Path classesDir) {
		Set<Class<?>> classes = new HashSet<>();
		File directory = classesDir.resolve(basePackage.replace('.', '/')).toFile();
		ClassLoader projectLoader = Thread.currentThread().getContextClassLoader();
		if (directory.exists() && directory.isDirectory()) {
			findClassesInDirectory(directory, basePackage, classes, projectLoader);
		} else {
			LOGGER.warn("No directory found for package: {}", basePackage);
		}
		return classes;
	}

	public static void findClassesInDirectory(final File directory, final String packageName, final Set<Class<?>> classes,
			final ClassLoader classLoader) {
		for (File file : Objects.requireNonNull(directory.listFiles())) {
			if (file.isDirectory()) {
				findClassesInDirectory(file, packageName + "." + file.getName(), classes, classLoader);
			} else if (file.getName().endsWith(".class")) {
				String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
				try {
					Class<?> cls = Class.forName(className, false, classLoader);
					classes.add(cls);
					LOGGER.debug("Found class: {}", className);
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException("Could not load class: " + className, e);
				}
			}
		}
	}

	public static <T> T convertFromStringArray(final String[] args, final InstanceFunction<T> instanceFunction) {
		List<Field> fields = Fields.getAllDeclared(GeneratorProperties.class);
		Map<String, String> fieldValueMap = HashMap.newHashMap(args.length);
		for (int i = 0; i < args.length; ++i) {
			fieldValueMap.put(fields.get(i).getName(), args[i]);
		}
		return MapConversions.convertFromMap(fieldValueMap, instanceFunction);
	}

	public static <T> T unsupportedOperation() {
		var e = new UnsupportedOperationException();
		LOGGER.error("Error", e);
		throw e;
	}
}
