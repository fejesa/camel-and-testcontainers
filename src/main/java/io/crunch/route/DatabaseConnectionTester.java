package io.crunch.route;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * A Camel route for testing database connectivity to source and target databases.
 *
 * <p>This route is conditionally executed based on the value of the {@code app.archive.command} configuration property.
 * When the command is set to {@code connection-test}, the routes verify connectivity to the configured source and target databases
 * by executing a simple SQL query. Errors during the connection tests are logged.</p>
 */
@ApplicationScoped
public class DatabaseConnectionTester extends RouteBuilder {

    @ConfigProperty(name = "app.archive.command")
    String command;

    /**
     * Configures the Camel routes for testing database connectivity.
     *
     * <p>This method defines the following routes:</p>
     * <ul>
     *   <li>A route for testing the connection to the source database, logging the results of the test.</li>
     *   <li>A route for testing the connection to the target database, logging the results of the test.</li>
     * </ul>
     *
     * <p>The connection tests are implemented by executing a simple {@code SELECT 1} SQL query on each database.</p>
     *
     * <p>Exceptions encountered during the tests are handled gracefully, with the error details logged.</p>
     */
    @Override
    public void configure() {
        if("connection-test".equalsIgnoreCase(command)) {
            // Define global exception handling for database connection errors
            onException(Exception.class).handled(true).log("Database connection error occurred: ${exception.message}");
            var sqlCommand = "select 1";

            // Route for testing the source database connection
            from("timer:source-database-test?delay=-1&repeatCount=1")
                .routeId("source-database-test")
                .log("-> Testing connection to Source Database: {{quarkus.datasource.source.jdbc.url}}")
                .setBody().simple(sqlCommand)
                .to("jdbc:source")
                .log("-> Connection to Source Database successful");

            // Route for testing the target database connection
            from("timer:target-database-test?delay=-1&repeatCount=1")
                .routeId("target-database-test")
                .log("-> Testing connection to Target Database: {{quarkus.datasource.target.jdbc.url}}")
                .setBody().simple(sqlCommand)
                .to("jdbc:target")
                .log("-> Connection to Target Database successful");
        }
    }
}
