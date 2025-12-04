package org.oogp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.oogp.jakarta.OpenApiSpecJakartaGenerator;
import org.oogp.spring.OpenApiSpecSpringDocGenerator;

/**
 * Mojo that generates an OpenAPI YAML file from the compiled Spring controllers.
 * <p>
 * This goal can be bound to the {@code process-classes} phase to ensure the classes are compiled but the application is
 * not started.
 *
 * @author Radu Sebastian LAZIN
 */
@Mojo(name = "generate-openapi", defaultPhase = LifecyclePhase.PROCESS_CLASSES, threadSafe = true)
public class OpenApiMojo extends AbstractMojo {

	/**
	 * The current maven project.
	 */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	/**
	 * The properties.
	 */
	@Parameter
	private GeneratorProperties properties;

	/**
	 * Default constructor.
	 */
	public OpenApiMojo() {
		// empty
	}

	/**
	 * @see #execute()
	 */
	@Override
	public void execute() throws MojoExecutionException {
		if (null == properties) {
			properties = new GeneratorProperties();
		}
		try (URLClassLoader projectClassLoader = buildProjectClassLoader()) {
			properties.applyDefaults(project);

			JavaEnvironment.info(getLog()::info);

			getLog().info("Generating OpenAPI spec...");
			getLog().info("   Classes directory: " + properties.getClassesDir());
			getLog().info("   Packages to scan: " + properties.getPackagesToScan());
			getLog().info("   Output: " + properties.getOutputFile());

			Path outputFilePath = Path.of(properties.getOutputFile());
			// ensure directories exist
			Files.createDirectories(outputFilePath.getParent());

			System.setProperty("project.build.outputDirectory", project.getBuild().getOutputDirectory());
			Thread.currentThread().setContextClassLoader(projectClassLoader);

			switch (ProjectType.fromString(properties.getProjectType())) {
				case JAKARTA -> OpenApiSpecJakartaGenerator.generate(properties);
				case SPRING -> OpenApiSpecSpringDocGenerator.generate(properties);
				default -> throw new UnsupportedOperationException("Unknown project type: " + properties.getProjectType());
			}
		} catch (Exception e) {
			getLog().info("Error generating OpenAPI spec: " + e.getMessage());
			throw new MojoExecutionException("Failed to generate OpenAPI spec", e);
		}
	}

	/**
	 * Builds the project class loader.
	 *
	 * @return the project class loader
	 * @throws IOException when an I/O error occurs
	 * @throws DependencyResolutionRequiredException when project resolution fails
	 */
	private URLClassLoader buildProjectClassLoader() throws IOException, DependencyResolutionRequiredException {
		List<String> elements = project.getRuntimeClasspathElements();
		List<URL> urls = new ArrayList<>(elements.size());
		for (String element : elements) {
			urls.add(new File(element).toURI().toURL());
		}
		return new URLClassLoader(urls.toArray(URL[]::new), getClass().getClassLoader());
	}
}
