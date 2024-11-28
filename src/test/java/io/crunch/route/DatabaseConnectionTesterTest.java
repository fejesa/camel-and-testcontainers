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

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(routeDefinitions.stream().map(RouteDefinition::getEndpointUrl).anyMatch(url -> url.equals("timer:source-database-test?delay=-1&repeatCount=1")));
        assertTrue(routeDefinitions.stream().map(RouteDefinition::getEndpointUrl).anyMatch(url -> url.equals("timer:target-database-test?delay=-1&repeatCount=1")));
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
