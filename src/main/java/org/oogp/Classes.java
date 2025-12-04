package org.oogp;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apiphany.lang.Strings;
import org.morphix.convert.MapConversions;
import org.morphix.lang.JavaObjects;
import org.morphix.reflection.Fields;
import org.morphix.reflection.InstanceCreator;
import org.morphix.reflection.ReflectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for class-related operations including class discovery, loading, and conversion.
 * <p>
 * The class uses the current thread's context class loader for class loading operations and relies on the
 * 'project.build.outputDirectory' system property to locate compiled classes.
 *
 * @author Radu Sebastian LAZIN
 */
public class Classes {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Classes.class);

	/**
	 * The file extension used for Java compiled class files. This constant represents the standard ".class" extension that
	 * is appended to compiled Java bytecode files.
	 */
	private static final String CLASS_FILE_EXTENSION = ".class";

	/**
	 * Hide constructor.
	 */
	private Classes() {
		// empty
	}

	/**
	 * Detects the project's build output directory containing compiled classes.
	 * <p>
	 * This method attempts to locate the directory where compiled class files are stored by reading the
	 * 'project.build.outputDirectory' system property, which is typically set by build tools like Maven.
	 *
	 * @return a Path object representing the project's build output directory
	 * @throws IllegalStateException if the 'project.build.outputDirectory' system property is not set or is empty,
	 *     indicating the classes directory could not be detected
	 */
	public static Path detectDirectory() {
		String buildOutput = System.getProperty("project.build.outputDirectory");
		if (Strings.isNotEmpty(buildOutput)) {
			return Path.of(buildOutput);
		}
		LOGGER.error("Missing 'project.build.outputDirectory' property");
		throw new IllegalStateException("Could not detect project classes directory");
	}

	/**
	 * Finds all classes within a specified package by scanning the file system directory structure.
	 * <p>
	 * This method resolves the package name to a directory path, verifies the directory exists, and then searches for all
	 * class files within that directory and its subdirectories.
	 *
	 * @param basePackage the package name to search for classes (e.g., "com.example.mypackage")
	 * @param classesDir the root directory path where compiled classes are located (typically target/classes)
	 * @return a Set of Class objects found in the specified package, or an empty Set if the package directory doesn't exist
	 * or no classes are found
	 * @throws SecurityException if a security manager exists and access to class loading is restricted
	 */
	public static Set<Class<?>> findInPackage(final String basePackage, final Path classesDir) {
		File directory = classesDir.resolve(basePackage.replace('.', '/')).toFile();
		if (!directory.exists() || !directory.isDirectory()) {
			LOGGER.warn("No directory found for package: {}", basePackage);
			return Collections.emptySet();
		}
		ClassLoader projectClassLoader = Thread.currentThread().getContextClassLoader();
		return findInDirectory(directory, basePackage, projectClassLoader);
	}

	/**
	 * Recursively searches for all class files in the specified directory and its subdirectories, loading them as Class
	 * objects using the provided ClassLoader.
	 * <p>
	 * This method performs a depth-first traversal of the directory structure, treating each subdirectory as a package
	 * component and each .class file as a loadable class.
	 *
	 * @param directory the root directory to search for class files (must not be null)
	 * @param packageName the base package name corresponding to the directory
	 * @param classLoader the ClassLoader to use for loading the discovered classes
	 * @return a Set containing all Class objects found in the directory tree
	 * @throws NullPointerException if the directory is null or if directory.listFiles() returns null
	 * @throws ReflectionException if any class fails to load (depends on getOne implementation)
	 */
	public static Set<Class<?>> findInDirectory(final File directory, final String packageName, final ClassLoader classLoader) {
		File[] files = Objects.requireNonNull(directory.listFiles());
		Set<Class<?>> classes = new HashSet<>();
		for (File file : files) {
			if (file.isDirectory()) {
				classes.addAll(findInDirectory(file, packageName + "." + file.getName(), classLoader));
			} else if (file.getName().endsWith(CLASS_FILE_EXTENSION)) {
				String className = packageName + '.' + file.getName().substring(0, file.getName().length() - CLASS_FILE_EXTENSION.length());
				Class<?> cls = getOne(className, classLoader);
				classes.add(cls);
			}
		}
		return classes;
	}

	/**
	 * Finds all classes within the specified packages that are annotated with at least one of the given annotations.
	 * <p>
	 * This method scans each package for classes, checks each class for the presence of any of the specified annotations, and
	 * collects those classes into a result set.
	 *
	 * @param packages the set of package names to scan for classes
	 * @param projectClassesDir the directory containing the compiled project classes
	 * @param annotations the set of annotation classes to look for on the classes
	 * @return a Set of Class objects that are annotated with at least one of the specified annotations
	 */
	static Set<Class<?>> findWithAnyAnnotation(final Set<String> packages, final Path projectClassesDir,
			final Set<Class<? extends Annotation>> annotations) {
		Set<Class<?>> classesWithAnnotations = new HashSet<>();
		for (String pkg : packages) {
			LOGGER.info("Scanning package: {}", pkg);
			Set<Class<?>> classes = findInPackage(pkg, projectClassesDir);
			for (Class<?> cls : classes) {
				boolean hasAtLeastOneOfTheAnnotations = false;
				for (Class<? extends Annotation> annotation : annotations) {
					boolean hasAnnotation = null != cls.getAnnotation(annotation);
					if (hasAnnotation) {
						LOGGER.info("Found {} on: {}", annotation, cls);
					}
					hasAtLeastOneOfTheAnnotations |= hasAnnotation;
				}
				if (hasAtLeastOneOfTheAnnotations) {
					classesWithAnnotations.add(cls);
				}
			}
		}
		return classesWithAnnotations;
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

	/**
	 * Returns a class based on a class name.
	 *
	 * @param <T> returned type
	 *
	 * @param className the {@linkplain ClassLoader##binary-name binary name}
	 * @param classLoader the class loader used to load the class
	 * @return a class based on a class name
	 * @throws ReflectionException if the class cannot be loaded
	 */
	public static <T> Class<T> getOne(final String className, final ClassLoader classLoader) {
		return org.morphix.reflection.Classes.getOne(className, classLoader);
	}

	/**
	 * Creates a mutable set containing the specified classes.
	 *
	 * @param classes the classes to include in the set
	 * @return a mutable Set containing the specified classes
	 */
	static Set<Class<?>> mutableSetOf(Class<?>... classes) {
		return org.morphix.reflection.Classes.mutableSetOf(classes);
	}

	/**
	 * Throws an UnsupportedOperationException and logs the error. This utility method is used to indicate that a particular
	 * operation is not supported or not yet implemented.
	 *
	 * @param <T> the return type (never actually returned due to exception)
	 *
	 * @return never returns as it always throws an exception
	 * @throws UnsupportedOperationException always thrown to indicate unsupported operation
	 */
	public static <T> T unsupportedOperation() {
		var e = new UnsupportedOperationException();
		LOGGER.error("Error", e);
		throw e;
	}
}
