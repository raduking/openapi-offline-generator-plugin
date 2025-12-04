# openapi-offline-generator-plugin
A Maven plugin for generating OpenAPI documentation offline.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.raduking/openapi-offline-generator-plugin)](https://central.sonatype.com/artifact/io.github.raduking/openapi-offline-generator-plugin)
[![GitHub Release](https://img.shields.io/github/v/release/raduking/openapi-offline-generator-plugin)](https://github.com/raduking/openapi-offline-generator-plugin/releases)
[![License](https://img.shields.io/github/license/raduking/openapi-offline-generator-plugin)](https://opensource.org/license/apache-2-0)
[![Java](https://img.shields.io/badge/Java-21+-blue)](https://www.oracle.com/java/technologies/downloads/#java21)
[![PRs](https://img.shields.io/github/issues-pr/raduking/openapi-offline-generator-plugin)](https://github.com/raduking/openapi-offline-generator-plugin/pulls)

#### Status

[![branch: master](https://img.shields.io/badge/branch-master-blue)](https://github.com/raduking/openapi-offline-generator-plugin/tree/master)
![Build (master)](https://github.com/raduking/openapi-offline-generator-plugin/actions/workflows/build.yml/badge.svg?branch=master)
[![branch: develop](https://img.shields.io/badge/branch-develop-purple)](https://github.com/raduking/openapi-offline-generator-plugin/tree/develop)
![Build (develop)](https://github.com/raduking/openapi-offline-generator-plugin/actions/workflows/build.yml/badge.svg?branch=develop)

## Features
- Pure offline generation (no network calls, no Spring context loading for Spring applications)
- Fast, reproducible, CI friendly
- Supports YAML, JSON bundle outputs
- Deterministic builds (stable ordering / hashing)

## Prerequisites
- Java 25+
- Maven 3.8+

## Quick Start
Add the plugin to your project POM:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.example</groupId>
            <artifactId>openapi-offline-generator-plugin</artifactId>
            <version>1.0.2</version>
            <executions>
                <execution>
                    <id>generate-openapi-offline</id>
                    <phase>process-classes</phase>
                    <goals>
                        <goal>generate-openapi</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <properties>
                    <!-- plugin properties -->
                </properties>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Run:
```
mvn clean verify
```
Artifacts will be placed under target/openapi.

## Configuration Options
| Field | Description | Default                                            |
|-------|-------------|----------------------------------------------------|
| `packagesToScan` | The base package(s) to scan for REST controllers  (multiple packages can be comma-separated) |
| `outputFile` | The output file for the generated OpenAPI definition | `${project.build.directory}/generated-openapi.yaml` |
| `classesDir` | The compiled classes directory (where Spring controllers are located) | `${project.build.outputDirectory}` |
| `schemaForObjectClass` | The schema to use when the schema implementation is `Object.class` | `object` |
| `projectType` | The project type (`spring`/`jakarta`) | `spring` | 
| `oauth2` | OAuth2 options (see below) | |
| `extensions` | OpenAPI extensions (as map) | |

### OAuth2 Options

| Field | Description | Default |
|-------|-------------|---------|
| `enabled` | Flag to enable/disable OAuth2 | `false` |
| `authorizationUrl` | The authorization URL | `http://automatically/replaced/on/runtime` |


### Examples

Minimal config:
```xml
<configuration>
    <properties>
        <outputFile>${project.basedir}/spec/openapi.yaml</outputFile>
    </properties>
</configuration>
```

OpenAPI extensions:
```xml
<configuration>
    <properties>
        <extensions>
            <x-internal-hostname>http://my-internal-host:8080</x-internal-hostname>
        </extensions>
    </properties>
</configuration>
```

## Build Lifecycle Integration
Typical phases:
1. prepare-resources: (optional preprocessing)
2. compile: (class compilation)
2. process-classes: plugin execution
3. package: include generated spec as artifact classifier if desired

Attach generated spec:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <executions>
        <execution>
            <id>attach-openapi</id>
            <phase>package</phase>
            <goals>
                <goal>resources</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.directory}/generated-spec</outputDirectory>
                <resources>
                    <resource>
                        <directory>${project.basedir}/spec</directory>
                        <includes>
                            <include>generated-openapi.yaml</include>
                        </includes>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Offline Philosophy
- All enrichment (bundling, ordering, validation) happens at build time
- Encourages contract-first development
- Makes specs diff-friendly for code review

## Roadmap
- Gradle plugin wrapper
- Multi-module aggregation
- OpenAPI 4.x (when finalized)
- Pluggable preprocessors

## Contributing
1. Fork
2. Create feature branch
3. Add tests (mvn test)
4. Run full build (mvn verify)
5. Open PR referencing any related issue

## Development

Snapshot install:
```
mvn clean install
```

## Release (Maintainers)
- Update [CHANGELOG](CHANGELOG.md)
- Set release version
- mvn clean deploy -Prelease

## License
[Apache License 2.0](LICENSE)

## Security
Report vulnerabilities via private issue or security email channel. Do not open public issues until disclosure window ends.

## Acknowledgements
Inspired by needs for deterministic, auditable API specification generation in regulated environments.

## Contact
Open an issue for bugs, discussions for ideas.
