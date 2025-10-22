package org.oogp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link OpenApiSpecSpringDocGenerator}.
 * <p>
 * This class will create/overwrite at least these 2 files:
 * <ul>
 * <li>open-api-with-generate.yaml</li>
 * <li>open-api-with-main.yaml</li>
 * </ul>
 * which are used as a marker to see the changes in generation between versions.
 *
 * @author raduking
 */
class OpenApiSpecSpringDocGeneratorTest {

	@Test
	void shouldBuildOpenApiFile() throws IOException {
		String currentDirectory = Paths.get("").toAbsolutePath().toString();
		System.setProperty("project.build.outputDirectory", currentDirectory + "/target/test-classes");

		String fileName = currentDirectory + "/src/test/resources/open-api-with-generate.yaml";
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		GeneratorProperties generatorProperties = new GeneratorProperties();
		generatorProperties.setPackagesToScan("org.oogp.controller");
		generatorProperties.setOutputFile(fileName);
		GeneratorProperties.OAuth2 oAuth2 = new GeneratorProperties.OAuth2();
		oAuth2.setEnabled(true);
		oAuth2.setAuthorizationUrl("http://automatically/replaced/on/runtime/by/unknown");
		generatorProperties.setOauth2(oAuth2);
		generatorProperties.setExtensions(Map.of("x-internal-hostname", "http://gst-partner-service:8080"));
		OpenApiSpecSpringDocGenerator.generate(generatorProperties);

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));
	}

	@Test
	void shouldBuildOpenApiFileWithMain() throws IOException {
		String currentDirectory = Paths.get("").toAbsolutePath().toString();
		System.setProperty("project.build.outputDirectory", currentDirectory + "/target/test-classes");

		String fileName = currentDirectory + "/src/test/resources/open-api-with-main.yaml";
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		OpenApiSpecSpringDocGenerator.main(new String[] { "org.oogp.controller", fileName });

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));
	}
}
