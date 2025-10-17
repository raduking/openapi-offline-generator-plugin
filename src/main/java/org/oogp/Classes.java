package org.oogp;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
		if (buildOutput != null && !buildOutput.isBlank()) {
			return Path.of(buildOutput);
		} else {
			LOGGER.warn("Missing 'project.build.outputDirectory' property");
		}
		throw new IllegalStateException("Could not detect project classes directory");
	}

	public static Set<Class<?>> findInPackage(String basePackage, Path classesDir) {
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

	public static void findClassesInDirectory(File directory, String packageName, Set<Class<?>> classes, ClassLoader classLoader) {
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

	public static <T> T unsupportedOperation() {
		var e = new UnsupportedOperationException();
		LOGGER.error("Error", e);
		throw e;
	}
}
