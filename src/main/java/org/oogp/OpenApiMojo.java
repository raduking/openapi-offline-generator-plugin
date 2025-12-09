package org.oogp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apiphany.json.JsonBuilder;

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
	 * The plugin artifacts.
	 */
	@Parameter(defaultValue = "${plugin.artifacts}", readonly = true, required = true)
	private List<Artifact> pluginArtifacts;

	/**
	 * The properties.
	 */
	@Parameter
	private GeneratorProperties properties;

	/**
	 * Whether to fork a new JVM process to run the generation.
	 */
	@Parameter(defaultValue = "true")
	private Boolean fork;

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
		try {
			properties.applyDefaults(project.getBuild().getDirectory(), project.getBuild().getOutputDirectory());

			JavaEnvironment.info(getLog()::info);
			getLog().info("Generating OpenAPI spec...");
			getLog().info("   Classes directory: " + properties.getClassesDir());
			getLog().info("   Packages to scan: " + properties.getPackagesToScan());
			getLog().info("   Output: " + properties.getOutputFile());

			Path outputFilePath = Path.of(properties.getOutputFile());
			// ensure directories exist
			Files.createDirectories(outputFilePath.getParent());
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to apply default properties", e);
		}

		if (fork) {
			runForked();
		} else {
			run();
		}
	}

	/**
	 * Runs the generation in the same Maven process.
	 *
	 * @throws MojoExecutionException when generation fails
	 */
	private void run() throws MojoExecutionException {
		try (URLClassLoader projectClassLoader = buildProjectClassLoader()) {
			System.setProperty("project.build.outputDirectory", project.getBuild().getOutputDirectory());
			Thread.currentThread().setContextClassLoader(projectClassLoader);
			OpenApiGenerator.generate(properties);
		} catch (Exception e) {
			getLog().info("Error generating OpenAPI spec: " + e.getMessage());
			throw new MojoExecutionException("Failed to generate OpenAPI spec", e);
		}
	}

	/**
	 * Runs the generation in a forked JVM process.
	 *
	 * @throws MojoExecutionException when generation fails
	 */
	private void runForked() throws MojoExecutionException {
		InputStream inputStream = null;
		try {
			Path tempPropertiesFile = Files.createTempFile("openapi-generator-properties", ".json");
			String json = JsonBuilder.toJson(properties);
			Files.writeString(tempPropertiesFile, json);

			List<String> cp = new ArrayList<>();
			cp.add(project.getBuild().getOutputDirectory());
			cp.addAll(project.getRuntimeClasspathElements());
			File pluginJar = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			cp.add(pluginJar.getAbsolutePath());
			for (Artifact a : pluginArtifacts) {
				cp.add(a.getFile().getAbsolutePath());
			}
			String classpath = String.join(File.pathSeparator, cp);

			List<String> cmd = getCmd(classpath, tempPropertiesFile);

			getLog().info("Forking JVM to generate OpenAPI spec...");
			ProcessBuilder processBuilder = new ProcessBuilder(cmd)
					.redirectErrorStream(true);
			Process process = processBuilder.start();

			inputStream = process.getInputStream();
			inputStream.transferTo(System.out);

			int exitCode = process.waitFor();

			Files.deleteIfExists(tempPropertiesFile);
			if (exitCode != 0) {
				throw new MojoExecutionException("Forked OpenAPI generation process exited with code " + exitCode);
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to fork OpenAPI generation process", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					getLog().warn("Failed to close process input stream", e);
				}
			}
		}
	}

	/**
	 * Builds the command to run the forked JVM process.
	 *
	 * @param classpath the classpath
	 * @param tempPropertiesFile the temporary properties file
	 * @return the command
	 */
	private List<String> getCmd(final String classpath, final Path tempPropertiesFile) {
		List<String> cmd = new ArrayList<>();
		cmd.add(JavaEnvironment.getJavaExecutablePath());
		cmd.add("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED");
		cmd.add("--add-opens=java.base/sun.reflect.annotation=ALL-UNNAMED");
		cmd.add("-cp");
		cmd.add(classpath);
		cmd.add("-Dproject.build.outputDirectory=" + project.getBuild().getOutputDirectory());
		cmd.add("-D" + JsonBuilder.Property.INDENT_OUTPUT + "=true");
		cmd.add(OpenApiGenerator.class.getName());
		cmd.add(tempPropertiesFile.toAbsolutePath().toString());
		return cmd;
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
