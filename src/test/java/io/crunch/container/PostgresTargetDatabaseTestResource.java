package io.crunch.container;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

/**
 * A Quarkus test resource lifecycle manager that sets up a PostgreSQL container for integration testing.
 * This class leverages Testcontainers to provide a pre-configured PostgreSQL instance with a JDBC URL,
 * which is made accessible to the test environment.
 *
 * <p>Note: Both {@link org.testcontainers.containers.PostgreSQLContainer} and
 * {@link io.quarkus.test.common.QuarkusTestResourceLifecycleManager} define {@code start()} and {@code stop()} methods.
 * To resolve this conflict, the PostgreSQL container is encapsulated within this class, ensuring proper integration
 * with Quarkus's test resource lifecycle management.</p>
 */
public class PostgresTargetDatabaseTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger logger = LoggerFactory.getLogger(PostgresTargetDatabaseTestResource.class);

    private final PostgreSQLContainer<?> container;

    /**
     * Constructs a new {@link PostgresTargetDatabaseTestResource} and initializes the PostgreSQL container.
     * The container is configured using values from the application configuration and a custom SQL script.
     */
    public PostgresTargetDatabaseTestResource() {
        var dockerImageName = DockerImageName.parse("postgres").withTag("15.0");
        container = new PostgreSQLContainer<>(dockerImageName)
                .withExposedPorts(getDbPort())
                .withDatabaseName(getDbName())
                .withUsername(getDbUser())
                .withPassword(getDbPassword())
                .withClasspathResourceMapping("init-target-db.sql",
                        "/docker-entrypoint-initdb.d/init-source-db.sql",
                        BindMode.READ_ONLY)
                .withLogConsumer(new Slf4jLogConsumer(logger)).waitingFor(Wait.forListeningPort());
    }

    /**
     * Starts the PostgreSQL container and returns configuration properties required by Quarkus tests.
     *
     * @return A map containing the JDBC URL for the PostgreSQL container and other test-specific configurations.
     */
    @Override
    public Map<String, String> start() {
        container.start();

        logger.info("The test target could be accessed through the following JDBC url: {}", container.getJdbcUrl());

        return Map.of("quarkus.datasource.target.jdbc.url", container.getJdbcUrl(),
                "timer.period", "100",
                "timer.delay", "0");
    }

    /**
     * Stops the PostgreSQL container to clean up resources after the tests are complete.
     */
    @Override
    public void stop() {
        try {
            if (container != null) {
                container.stop();
            }
        } catch (Exception ex) {
            logger.error("An issue occurred while stopping the targetDbContainer", ex);
        }
    }

    /**
     * Retrieves the database password from the application configuration.
     *
     * @return The database password as a {@link String}.
     */
    private String getDbPassword() {
        return ConfigProvider.getConfig().getValue("datasource.target.password", String.class);
    }

    /**
     * Retrieves the database username from the application configuration.
     *
     * @return The database username as a {@link String}.
     */
    private String getDbUser() {
        return ConfigProvider.getConfig().getValue("datasource.target.username", String.class);
    }

    /**
     * Retrieves the database name from the application configuration.
     *
     * @return The database name as a {@link String}.
     */
    private String getDbName() {
        return ConfigProvider.getConfig().getValue("datasource.target.dbname", String.class);
    }

    /**
     * Retrieves the database port from the application configuration.
     *
     * @return The database port as an {@link Integer}.
     */
    private Integer getDbPort() {
        return ConfigProvider.getConfig().getValue("datasource.target.port", Integer.class);
    }
}
