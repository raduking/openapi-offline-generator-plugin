package org.oogp;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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
	 * Default constructor.
	 */
	public OpenApiMojo() {
		// empty
	}

	@Override
	public void execute() throws MojoExecutionException {
		try {
			getLog().info("Generating OpenAPI spec...");
			getLog().info("   Classes directory: " + classesDir);
			getLog().info("   Packages to scan: " + packagesToScan);
			getLog().info("   Output: " + outputFile);

			// Ensure directories exist
			Files.createDirectories(Path.of(outputFile).getParent());

			OpenApiSpecGenerator.generate(packagesToScan, outputFile);

			getLog().info("OpenAPI spec successfully generated at: " + outputFile);
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to generate OpenAPI spec", e);
		}
	}
}
