package org.oogp;

import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apiphany.json.JsonBuilder;
import org.apiphany.lang.Strings;

/**
 * All the configurable properties in the generator.
 *
 * @author Radu Sebastian LAZIN
 */
public class GeneratorProperties {

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
	 * Flag to enable OAuth2 security scheme.
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
	 * Fills in default values for missing fields using MavenProject context.
	 *
	 * @param project the Maven project
	 */
	public void applyDefaults(final MavenProject project) {
		if (Strings.isEmpty(outputFile)) {
			outputFile = project.getBuild().getDirectory() + "/generated-openapi.yaml";
		}
		if (Strings.isEmpty(classesDir)) {
			classesDir = project.getBuild().getOutputDirectory();
		}
		if (Strings.isEmpty(projectType)) {
			projectType = "spring";
		}
		if (oauth2 == null) {
			oauth2 = new OAuth2();
			oauth2.applyDefaults(project);
		}
	}

	/**
	 * @see #toString()
	 */
	@Override
	public String toString() {
		return JsonBuilder.toJson(this);
	}

	/**
	 * @return the classesDir
	 */
	public String getClassesDir() {
		return classesDir;
	}

	/**
	 * @param classesDir the classesDir to set
	 */
	public void setClassesDir(final String classesDir) {
		this.classesDir = classesDir;
	}

	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(final String outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * @return the packagesToScan
	 */
	public String getPackagesToScan() {
		return packagesToScan;
	}

	/**
	 * @param packagesToScan the packagesToScan to set
	 */
	public void setPackagesToScan(final String packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	/**
	 * @return the projectType
	 */
	public String getProjectType() {
		return projectType;
	}

	/**
	 * @param projectType the projectType to set
	 */
	public void setProjectType(final String projectType) {
		this.projectType = projectType;
	}

	/**
	 * @return the oauth2
	 */
	public OAuth2 getOauth2() {
		return oauth2;
	}

	/**
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
	public boolean isOAuth2Enabled() {
		return oauth2 != null && oauth2.isEnabled();
	}

	/**
	 * @return the extensions
	 */
	public Map<String, String> getExtensions() {
		return extensions;
	}

	/**
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
		 *
		 * @param project the Maven project
		 */
		public void applyDefaults(final MavenProject project) {
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

}
