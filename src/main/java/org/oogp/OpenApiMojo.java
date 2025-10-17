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
	 * The compiled classes directory (where Spring controllers are located).
	 */
	@Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true)
	private String classesDir;

	/**
	 * The output file for the generated OpenAPI definition.
	 */
	@Parameter(defaultValue = "${project.build.directory}/generated-openapi.yaml", required = true)
	private String outputFile;

	/**
	 * The base package(s) to scan for REST controllers. Multiple packages can be comma-separated.
	 */
	@Parameter(required = true)
	private String packagesToScan;

	/**
	 * The project type ("spring"/"jakarta"), default being "spring".
	 */
	@Parameter(defaultValue = "spring")
	private String projectType;

	/**
	 * The current maven project.
	 */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

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
		try (URLClassLoader projectClassLoader = buildProjectClassLoader()) {
			getLog().info("Generating OpenAPI spec...");
			getLog().info("   Classes directory: " + classesDir);
			getLog().info("   Packages to scan: " + packagesToScan);
			getLog().info("   Output: " + outputFile);

			// ensure directories exist
			Files.createDirectories(Path.of(outputFile).getParent());

			System.setProperty("project.build.outputDirectory", project.getBuild().getOutputDirectory());
			Thread.currentThread().setContextClassLoader(projectClassLoader);

			switch (ProjectType.fromString(projectType)) {
				case SPRING -> OpenApiSpecSpringDocGenerator.generate(packagesToScan, outputFile);
				case JAKARTA -> OpenApiSpecJakartaGenerator.generate(packagesToScan, outputFile);
				default -> throw new UnsupportedOperationException("Unknown project type: " + projectType);
			}

		} catch (Exception e) {
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
