package io.crunch.route;

import io.crunch.container.PostgresSourceDatabaseTestResource;
import io.crunch.container.PostgresTargetDatabaseTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.apache.camel.model.RouteDefinition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * <b>All</b> {@code QuarkusTestResource} annotations in the test module
 * are discovered (regardless of the test which contains the annotation)
 * and their corresponding {@link io.quarkus.test.common.QuarkusTestResourceLifecycleManager}
 * started <b>before</b> <b>any</b> test is run.
 */
@QuarkusTest
@TestProfile(DatabaseConnectionTesterTest.DatabaseConnectionTesterProfile.class)
@QuarkusTestResource(PostgresSourceDatabaseTestResource.class)
@QuarkusTestResource(PostgresTargetDatabaseTestResource.class)
class DatabaseConnectionTesterTest {

    @Inject
    DatabaseConnectionTester databaseConnectionTester;

    @Test
    void checkConnectionTestRoutesEndpoint() {
        var routeDefinitions = databaseConnectionTester.getRouteCollection().getRoutes();
        assertThat(routeDefinitions.stream()
            .map(RouteDefinition::getEndpointUrl)
            .anyMatch("timer:source-database-test?delay=-1&repeatCount=1"::equals)).isTrue();
        assertThat(routeDefinitions.stream()
            .map(RouteDefinition::getEndpointUrl)
            .anyMatch("timer:target-database-test?delay=-1&repeatCount=1"::equals)).isTrue();
    }

    public static class DatabaseConnectionTesterProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "app.archive.command", "connection-test",
                    "app.archive.year", "2024");
        }
    }
}
