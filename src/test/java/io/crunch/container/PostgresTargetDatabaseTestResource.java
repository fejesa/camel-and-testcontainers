package io.crunch.container;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.Map;

import static org.apache.camel.util.CollectionHelper.mapOf;

public class PostgresTargetDatabaseTestResource<T extends GenericContainer<?>> implements QuarkusTestResourceLifecycleManager {

    private static final Logger logger = LoggerFactory.getLogger(PostgresTargetDatabaseTestResource.class);

    private static final String POSTGRES_IMAGE = "docker.io/postgres:15.0";

    private GenericContainer<?> targetDbContainer;

    @Override
    public Map<String, String> start() {
        var postgresPort = ConfigProvider.getConfig().getValue("datasource.target.port", Integer.class);
        var postgresDbName = ConfigProvider.getConfig().getValue("datasource.target.dbname", String.class);
        var postgresDbUser = ConfigProvider.getConfig().getValue("datasource.target.username", String.class);
        var postgresDbPassword = ConfigProvider.getConfig().getValue("datasource.target.password", String.class);

        logger.info(TestcontainersConfiguration.getInstance().toString());

        targetDbContainer = new GenericContainer<>(POSTGRES_IMAGE)
            .withExposedPorts(postgresPort)
            .withEnv("POSTGRES_USER", postgresDbUser)
            .withEnv("POSTGRES_PASSWORD", postgresDbPassword)
            .withEnv("POSTGRES_DB", postgresDbName)
            .withClasspathResourceMapping("init-target-db.sql",
                    "/docker-entrypoint-initdb.d/init-source-db.sql",
                    BindMode.READ_ONLY)
            .withLogConsumer(new Slf4jLogConsumer(logger)).waitingFor(Wait.forListeningPort());
        targetDbContainer.start();

        var targetJDBCUrl = "jdbc:postgresql://%s:%s/%s".formatted(
            targetDbContainer.getHost(),
            targetDbContainer.getMappedPort(postgresPort),
            postgresDbName);
        logger.info("The test target could be accessed through the following JDBC url: {}", targetJDBCUrl);

        return mapOf("quarkus.datasource.target.jdbc.url", targetJDBCUrl,
                "timer.period", "100",
                "timer.delay", "0");
    }

    @Override
    public void stop() {
        try {
            if (targetDbContainer != null) {
                targetDbContainer.stop();
            }
        } catch (Exception ex) {
            logger.error("An issue occurred while stopping the targetDbContainer", ex);
        }
    }
}
