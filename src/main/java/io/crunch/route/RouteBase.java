package io.crunch.route;

import io.crunch.template.SqlTemplates;
import jakarta.inject.Inject;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public abstract class RouteBase extends RouteBuilder {

    @Inject
    SqlTemplates sqlTemplates;

    @ConfigProperty(name = "app.archive.command", defaultValue = "connection-test")
    String command;

    @ConfigProperty(name = "app.archive.year")
    int year;

    String getSqlTemplate(String templateFileName) throws IOException, URISyntaxException {
        return sqlTemplates.getSqlTemplate(year, templateFileName);
    }

    Map<String, Expression> getJdbcParameters(String templateFileName) throws IOException, URISyntaxException {
        return sqlTemplates.getJdbcParameterExpressions(year, templateFileName, this);
    }

    boolean isTriggered(String routeCommand) {
        return routeCommand.equalsIgnoreCase(command);
    }
}
