package org.oogp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.oogp.spring.OpenApiSpecSpringDocGenerator;

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

	private static final String OUTPUT_FILE_NAME_WITH_GENERATE = "open-api-with-generate.yaml";
	private static final String OUTPUT_FILE_NAME_WITH_MAIN = "open-api-with-main.yaml";
	private static final String OUTPUT_FILE_NAME_FOR_OBJECT = "object.yaml";

	private static GeneratorProperties getGeneratorProperties(final String fileName) {
		GeneratorProperties generatorProperties = new GeneratorProperties();
		generatorProperties.setPackagesToScan("org.oogp.controller");
		generatorProperties.setOutputFile(fileName);
		generatorProperties.setSchemaForObjectClass("object");
		GeneratorProperties.OAuth2 oAuth2 = new GeneratorProperties.OAuth2();
		oAuth2.setEnabled(true);
		oAuth2.setAuthorizationUrl("http://automatically/replaced/on/runtime/by/unknown");
		generatorProperties.setOauth2(oAuth2);
		generatorProperties.setExtensions(Map.of("x-internal-hostname", "http://my-service-name:8080"));
		return generatorProperties;
	}

	@Test
	void shouldBuildOpenApiFileWithGenerate() throws IOException {
		String currentDirectory = Paths.get("").toAbsolutePath().toString();
		System.setProperty("project.build.outputDirectory", currentDirectory + "/target/test-classes");

		String fileName = currentDirectory + "/src/test/resources/" + OUTPUT_FILE_NAME_WITH_GENERATE;
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		GeneratorProperties generatorProperties = getGeneratorProperties(fileName);
		OpenApiSpecSpringDocGenerator.generate(generatorProperties);

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));
	}

	@Test
	void shouldBuildExpectedOpenApiFile() throws IOException {
		String currentDirectory = Paths.get("").toAbsolutePath().toString();
		System.setProperty("project.build.outputDirectory", currentDirectory + "/target/test-classes");

		String fileName = currentDirectory + "/src/test/resources/actual/" + OUTPUT_FILE_NAME_WITH_GENERATE;
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		GeneratorProperties generatorProperties = getGeneratorProperties(fileName);
		OpenApiSpecSpringDocGenerator.generate(generatorProperties);

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));

		String expectedFileName = currentDirectory + "/src/test/resources/expected/" + OUTPUT_FILE_NAME_WITH_GENERATE;
		String expectedContent = Files.readString(Paths.get(expectedFileName));
		String actualContent = Files.readString(path);

		assertThat(actualContent, equalTo(expectedContent));
	}

	@Test
	void shouldBuildOpenApiFileWithMain() throws IOException {
		String currentDirectory = Paths.get("").toAbsolutePath().toString();
		System.setProperty("project.build.outputDirectory", currentDirectory + "/target/test-classes");

		String fileName = currentDirectory + "/src/test/resources/" + OUTPUT_FILE_NAME_WITH_MAIN;
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		OpenApiSpecSpringDocGenerator.main(new String[] { "org.oogp.controller", fileName });

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));
	}

	@Test
	void shouldBuildOpenApiFileForObjectType() throws IOException {
		String currentDirectory = Paths.get("").toAbsolutePath().toString();
		System.setProperty("project.build.outputDirectory", currentDirectory + "/target/test-classes");

		String fileName = currentDirectory + "/src/test/resources/isolated/" + OUTPUT_FILE_NAME_FOR_OBJECT;
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		GeneratorProperties generatorProperties = new GeneratorProperties();
		generatorProperties.setPackagesToScan("org.oogp.object.controller");
		generatorProperties.setOutputFile(fileName);
		generatorProperties.setSchemaForObjectClass("object");
		OpenApiSpecSpringDocGenerator.generate(generatorProperties);

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));

		String expectedFileName = currentDirectory + "/src/test/resources/expected/isolated/" + OUTPUT_FILE_NAME_FOR_OBJECT;
		String expectedContent = Files.readString(Paths.get(expectedFileName));
		String actualContent = Files.readString(path);

		assertThat(actualContent, equalTo(expectedContent));
	}

}
