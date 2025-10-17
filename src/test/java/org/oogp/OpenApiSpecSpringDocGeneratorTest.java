package org.oogp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class OpenApiSpecSpringDocGeneratorTest {

	@Test
	void shouldBuildOpenApiFile() throws IOException {
		String currentDirectory = Paths.get("").toAbsolutePath().toString();
		System.setProperty("project.build.outputDirectory", currentDirectory + "/target/test-classes");

		String fileName = currentDirectory + "/src/test/resources/open-api.yaml";
		Path path = Paths.get(fileName);
		Files.deleteIfExists(path);

		OpenApiSpecSpringDocGenerator.generate("org.oogp", fileName);

		boolean exists = Files.exists(path);

		assertThat(exists, equalTo(true));
	}

}
