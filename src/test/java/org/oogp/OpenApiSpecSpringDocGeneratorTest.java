package org.oogp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
@ExtendWith(MockitoExtension.class)
class OpenApiSpecSpringDocGeneratorTest {

	private static final String MY_SERVICE_PATH = "/src/test/resources/myservice/";
	private static final String ISOLATED_PATH = "/src/test/resources/isolated/";

	private static final String ACTUAL_PATH = "actual/";
	private static final String EXPECTED_PATH = "expected/";

	private static final String OUTPUT_FILE_NAME_WITH_GENERATE = "open-api-with-generate.yaml";
	private static final String OUTPUT_FILE_NAME_WITH_MAIN = "open-api-with-main.yaml";

	private static final String OUTPUT_FILE_NAME_FOR_OBJECT = "object.yaml";
	private static final String OUTPUT_FILE_NAME_FOR_OFFSET_DATE_TIME = "offsetdatetime.yaml";

	private String currentDirectory;

	@BeforeEach
	void setup() {
		currentDirectory = getCurrentDirectory();
		System.setProperty("project.build.outputDirectory", currentDirectory + "/target/test-classes");
	}

	private static GeneratorProperties getGeneratorProperties(final String outputFileName, final String packagesToScan) {
		GeneratorProperties generatorProperties = new GeneratorProperties();
		generatorProperties.setPackagesToScan(packagesToScan);
		generatorProperties.setOutputFile(outputFileName);
		generatorProperties.setSchemaForObjectClass("object");
		GeneratorProperties.OAuth2 oAuth2 = new GeneratorProperties.OAuth2();
		oAuth2.setEnabled(true);
		oAuth2.setAuthorizationUrl("http://automatically/replaced/on/runtime/by/unknown");
		generatorProperties.setOauth2(oAuth2);
		generatorProperties.setExtensions(Map.of("x-internal-hostname", "http://my-service-name:8080"));
		return generatorProperties;
	}

	private static String getCurrentDirectory() {
		return Paths.get("").toAbsolutePath().toString();
	}

	@Test
	void shouldBuildOpenApiFileWithGenerate() throws IOException {
		String fileName = currentDirectory + "/src/test/resources/" + OUTPUT_FILE_NAME_WITH_GENERATE;
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		String packagesToScan = "org.oogp.myservice.controller";
		GeneratorProperties generatorProperties = getGeneratorProperties(fileName, packagesToScan);

		OpenApiSpecSpringDocGenerator.generate(generatorProperties);

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));
	}

	@Test
	void shouldBuildOpenApiFileWithMain() throws IOException {
		System.setProperty("project.build.outputDirectory", currentDirectory + "/target/test-classes");

		String fileName = currentDirectory + "/src/test/resources/" + OUTPUT_FILE_NAME_WITH_MAIN;
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		OpenApiSpecSpringDocGenerator.main(new String[] { "org.oogp.myservice.controller", fileName });

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));
	}

	@Test
	void shouldBuildExpectedOpenApiFile() throws IOException {
		String fileName = currentDirectory + MY_SERVICE_PATH + ACTUAL_PATH + OUTPUT_FILE_NAME_WITH_GENERATE;
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		String packagesToScan = "org.oogp.myservice.controller";
		GeneratorProperties generatorProperties = getGeneratorProperties(fileName, packagesToScan);

		OpenApiSpecSpringDocGenerator.generate(generatorProperties);

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));

		String expectedFileName = currentDirectory + MY_SERVICE_PATH + EXPECTED_PATH + OUTPUT_FILE_NAME_WITH_GENERATE;
		String expectedContent = Files.readString(Paths.get(expectedFileName));
		String actualContent = Files.readString(path);

		assertThat(actualContent, equalTo(expectedContent));
	}

	@Test
	void shouldBuildOpenApiFileForObjectType() throws IOException {
		String fileName = currentDirectory + ISOLATED_PATH + ACTUAL_PATH + OUTPUT_FILE_NAME_FOR_OBJECT;
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		GeneratorProperties generatorProperties = new GeneratorProperties();
		generatorProperties.setPackagesToScan("org.oogp.isolated.object.controller");
		generatorProperties.setOutputFile(fileName);
		generatorProperties.setSchemaForObjectClass("object");
		OpenApiSpecSpringDocGenerator.generate(generatorProperties);

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));

		String expectedFileName = currentDirectory + ISOLATED_PATH + EXPECTED_PATH + OUTPUT_FILE_NAME_FOR_OBJECT;
		String expectedContent = Files.readString(Paths.get(expectedFileName));
		String actualContent = Files.readString(path);

		assertThat(actualContent, equalTo(expectedContent));
	}

	@Test
	void shouldBuildOpenApiFileForObjectTypeAndOffsetDateTime() throws IOException {
		String fileName = currentDirectory + ISOLATED_PATH + ACTUAL_PATH + OUTPUT_FILE_NAME_FOR_OFFSET_DATE_TIME;
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		GeneratorProperties generatorProperties = new GeneratorProperties();
		generatorProperties.setPackagesToScan("org.oogp.isolated.offsetdatetime.controller");
		generatorProperties.setOutputFile(fileName);
		generatorProperties.setSchemaForObjectClass("object");
		OpenApiSpecSpringDocGenerator.generate(generatorProperties);

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));

		String expectedFileName = currentDirectory + ISOLATED_PATH + EXPECTED_PATH + OUTPUT_FILE_NAME_FOR_OFFSET_DATE_TIME;
		String expectedContent = Files.readString(Paths.get(expectedFileName));
		String actualContent = Files.readString(path);

		assertThat(actualContent, equalTo(expectedContent));
	}
}
