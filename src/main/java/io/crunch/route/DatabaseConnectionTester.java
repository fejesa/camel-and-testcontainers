package io.crunch.route;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DatabaseConnectionTester extends RouteBuilder {

    @ConfigProperty(name = "app.archive.command")
    String command;

    @Override
    public void configure() {
        if("connection-test".equalsIgnoreCase(command)) {
            onException(Exception.class).handled(true).log("Database connection error occurred: ${exception.message}");
            var sqlCommand = "select 1";
            from("timer:source-database-test?delay=-1&repeatCount=1")
                .routeId("source-database-test")
                .log("-> Testing connection to Source Database: {{quarkus.datasource.source.jdbc.url}}")
                .setBody().simple(sqlCommand)
                .to("jdbc:source")
                .log("-> Connection to Source Database successful");

            from("timer:target-database-test?delay=-1&repeatCount=1")
                .routeId("target-database-test")
                .log("-> Testing connection to Target Database: {{quarkus.datasource.target.jdbc.url}}")
                .setBody().simple(sqlCommand)
                .to("jdbc:target")
                .log("-> Connection to Target Database successful");
        }
    }
}
