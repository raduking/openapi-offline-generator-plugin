package org.oogp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;
import org.apiphany.json.JsonBuilder;
import org.apiphany.lang.Strings;
import org.apiphany.lang.annotation.Ignored;
import org.apiphany.lang.collections.Lists;

/**
 * All the configurable properties in the generator.
 *
 * @author Radu Sebastian LAZIN
 */
public class GeneratorProperties {

	/**
	 * Defaults name space class.
	 *
	 * @author Radu Sebastian LAZIN
	 */
	public static class Default {

		/**
		 * The default build directory.
		 */
		public static final String BUILD_DIRECTORY = "target";

		/**
		 * The default classes directory.
		 */
		public static final String OUTPUT_DIRECTORY = "target/classes";

		/**
		 * The default generated OpenAPI YAML file.
		 */
		public static final String GENERATED_OPENAPI_FILE_NAME = "generated-openapi.yaml";

		/**
		 * The schema name for {@link Object} class.
		 */
		public static final String SCHEMA_FOR_OBJECT_CLASS = "object";

		/**
		 * Hide constructor.
		 */
		private Default() {
			// empty
		}
	}

	/**
	 * The base package(s) to scan for REST controllers. Multiple packages can be comma-separated.
	 */
	@Parameter(required = true)
	private String packagesToScan;

	/**
	 * The output file for the generated OpenAPI definition.
	 */
	@Parameter(required = true)
	private String outputFile;

	/**
	 * The compiled classes directory (where Spring controllers are located).
	 */
	@Parameter(readonly = true)
	private String classesDir;

	/**
	 * The project type ("spring"/"jakarta"), default being "spring".
	 */
	@Parameter
	private String projectType;

	/**
	 * The schema value for {@link Object} class.
	 */
	@Parameter
	private String schemaForObjectClass;

	/**
	 * Server properties.
	 */
	@Parameter
	private List<Server> servers;

	/**
	 * OAuth2 properties.
	 */
	@Parameter
	private OAuth2 oauth2;

	/**
	 * Open API extensions.
	 */
	@Parameter
	private Map<String, String> extensions;

	/**
	 * Default constructor.
	 */
	public GeneratorProperties() {
		// empty
	}

	/**
	 * Fills in default values for missing fields.
	 *
	 * @param projectBuildDirectory the project build directory
	 * @param projectBuildOutputDirectory the project build output directory
	 */
	public void applyDefaults(String projectBuildDirectory, String projectBuildOutputDirectory) {
		if (Strings.isEmpty(outputFile)) {
			String buildDir = projectBuildDirectory != null ? projectBuildDirectory : Default.BUILD_DIRECTORY;
			outputFile = buildDir + "/" + Default.GENERATED_OPENAPI_FILE_NAME;
		}
		if (Strings.isEmpty(classesDir)) {
			classesDir = projectBuildOutputDirectory != null ? projectBuildOutputDirectory : Default.OUTPUT_DIRECTORY;
		}
		if (Strings.isEmpty(projectType)) {
			projectType = "spring";
		}
		if (Strings.isEmpty(schemaForObjectClass)) {
			schemaForObjectClass = Default.SCHEMA_FOR_OBJECT_CLASS;
		} else {
			schemaForObjectClass = schemaForObjectClass.toLowerCase();
		}
		if (Lists.isEmpty(servers)) {
			servers = new ArrayList<>();
			Server defaultServer = new Server();
			defaultServer.applyDefaults();
			servers.add(defaultServer);
		}
		if (null == oauth2) {
			oauth2 = new OAuth2();
		}
		oauth2.applyDefaults();
	}

	/**
	 * @see #toString()
	 */
	@Override
	public String toString() {
		return JsonBuilder.toJson(this);
	}

	/**
	 * Returns the classes directory.
	 *
	 * @return the classesDir
	 */
	public String getClassesDir() {
		return classesDir;
	}

	/**
	 * Sets the classes directory.
	 *
	 * @param classesDir the classesDir to set
	 */
	public void setClassesDir(final String classesDir) {
		this.classesDir = classesDir;
	}

	/**
	 * Returns the output file.
	 *
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * Sets the output file.
	 *
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(final String outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * Returns the packages to scan.
	 *
	 * @return the packagesToScan
	 */
	public String getPackagesToScan() {
		return packagesToScan;
	}

	/**
	 * Sets the packages to scan.
	 *
	 * @param packagesToScan the packagesToScan to set
	 */
	public void setPackagesToScan(final String packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	/**
	 * Returns the project type.
	 *
	 * @return the projectType
	 */
	public String getProjectType() {
		return projectType;
	}

	/**
	 * Sets the project type.
	 *
	 * @param projectType the projectType to set
	 */
	public void setProjectType(final String projectType) {
		this.projectType = projectType;
	}

	/**
	 * Returns the schema for Object class.
	 *
	 * @return the schemaForObjectClass
	 */
	public String getSchemaForObjectClass() {
		return schemaForObjectClass;
	}

	/**
	 * Sets the schema for Object class.
	 *
	 * @param schemaForObjectClass the schemaForObjectClass to set
	 */
	public void setSchemaForObjectClass(String schemaForObjectClass) {
		this.schemaForObjectClass = schemaForObjectClass;
	}

	/**
	 * Returns the servers configurations.
	 *
	 * @return the servers
	 */
	public List<Server> getServers() {
		return servers;
	}

	/**
	 * Sets the servers configurations.
	 *
	 * @param servers the servers to set
	 */
	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

	/**
	 * Returns the OAuth2 configurations.
	 *
	 * @return the oauth2
	 */
	public OAuth2 getOauth2() {
		return oauth2;
	}

	/**
	 * Sets the OAuth2 configurations.
	 *
	 * @param oauth2 the oauth2 to set
	 */
	public void setOauth2(final OAuth2 oauth2) {
		this.oauth2 = oauth2;
	}

	/**
	 * Returns true if OAuth2 is enabled, false otherwise.
	 *
	 * @return true if OAuth2 is enabled, false otherwise
	 */
	@Ignored
	public boolean isOAuth2Enabled() {
		return oauth2 != null && oauth2.isEnabled();
	}

	/**
	 * Returns the extensions.
	 *
	 * @return the extensions
	 */
	public Map<String, String> getExtensions() {
		return extensions;
	}

	/**
	 * Sets the extensions.
	 *
	 * @param extensions the extensions to set
	 */
	public void setExtensions(final Map<String, String> extensions) {
		this.extensions = extensions;
	}

	/**
	 * The OAuth2 configurations.
	 *
	 * @author Radu Sebastian LAZIN
	 */
	public static class OAuth2 {

		/**
		 * Flag to enable/disable OAuth2.
		 */
		@Parameter
		private boolean enabled;

		/**
		 * The authorization URL.
		 */
		@Parameter
		private String authorizationUrl;

		/**
		 * Default constructor.
		 */
		public OAuth2() {
			// empty
		}

		/**
		 * Fills in default values for missing fields using MavenProject context.
		 */
		public void applyDefaults() {
			if (Strings.isEmpty(authorizationUrl)) {
				authorizationUrl = "http://automatically/replaced/on/runtime";
			}
		}

		/**
		 * Returns true if OAuth2 is enabled, false otherwise.
		 *
		 * @return true if OAuth2 is enabled, false otherwise
		 */
		public boolean isEnabled() {
			return enabled;
		}

		/**
		 * Sets the enabled flag.
		 *
		 * @param enabled the enabled flag to set
		 */
		public void setEnabled(final boolean enabled) {
			this.enabled = enabled;
		}

		/**
		 * Returns the authorization URL.
		 *
		 * @return the authorization URL
		 */
		public String getAuthorizationUrl() {
			return authorizationUrl;
		}

		/**
		 * Sets the authorization URL.
		 *
		 * @param authorizationUrl the authorization URL to set
		 */
		public void setAuthorizationUrl(final String authorizationUrl) {
			this.authorizationUrl = authorizationUrl;
		}
	}

	/**
	 * The Servers configurations.
	 *
	 * @author Radu Sebastian LAZIN
	 */
	public static class Server {

		/**
		 * The server URL.
		 */
		@Parameter
		private String url;

		/**
		 * Default constructor.
		 */
		public Server() {
			// empty
		}

		/**
		 * Fills in default values for missing fields using MavenProject context.
		 */
		public void applyDefaults() {
			if (Strings.isEmpty(url)) {
				url = "/";
			}
		}

		/**
		 * Returns the server URL.
		 *
		 * @return the server URL
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * Sets the server URL.
		 *
		 * @param url the server URL to set
		 */
		public void setUrl(final String url) {
			this.url = url;
		}
	}
}
