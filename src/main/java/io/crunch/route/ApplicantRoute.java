package io.crunch.route;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * A Camel route for extracting, transforming, and loading (ETL) applicant data between source and target databases.
 *
 * <p>This route performs the following operations:</p>
 * <ul>
 *   <li>Extracts data from a source database using a SQL query defined in a template file.</li>
 *   <li>Transforms the extracted data to include additional fields, such as the archiving time and year.</li>
 *   <li>Loads the transformed data into a target database using a parameterized SQL query.</li>
 * </ul>
 *
 * <p>The route is triggered based on a configuration property and executes a one-time operation.</p>
 * @apiNote This implementation does not support batch processing.
 */
@ApplicationScoped
public class ApplicantRoute extends RouteBase {

    /** The name of the SQL template file for querying the source database. */
    private static final String SOURCE_QUERY_TEMPLATE = "applicant-source-query.sql";

    /** The name of the SQL template file for the target database insertion query. */
    private static final String TARGET_QUERY_TEMPLATE = "applicant-target-query.sql";

    /**
     * Configures the Camel route for loading applicant data.
     *
     * <p>This method sets up the route to:
     * <ul>
     *   <li>Start with a timer-based trigger (if enabled via configuration).</li>
     *   <li>Extract data from the source database using the source query template.</li>
     *   <li>Transform the data to include additional fields.</li>
     *   <li>Load the transformed data into the target database using the target prepared statement template.</li>
     * </ul>
     * </p>
     *
     * @throws Exception if any error occurs while configuring the route.
     */
    @Override
    public void configure() throws Exception {
        if(isTriggered("load-applicant")) {
            // delay: The number of milliseconds to wait before the first event is generated. delay=-1 means the route is triggered immediately
            // repeatCount: The number of times the event is generated. repeatCount=1 means the route is triggered only once
            from("timer://load-applicant?delay=-1&repeatCount=1")
                .routeId("applicant-route")
                .setBody()
                    .simple(getTemplate(SOURCE_QUERY_TEMPLATE))
                    .log("-> Extracting data from Source Database {{quarkus.datasource.source.jdbc.url}}, SQL command: ${body}")
                .to("jdbc:source")
                .split(body())
                .process(this::transform)
                    .log("-> Transforming applicant: ${body[app_id]}")
                    // The query parameters are set as headers in the message, and the SQL command is set as the message body.
                    .setHeaders(getExpressions(TARGET_QUERY_TEMPLATE))
                    .setBody(constant(getTemplate(TARGET_QUERY_TEMPLATE)))
                    .log("-> Loading transformed data in target database, SQL command: ${body}")
                // useHeadersAsParameters: Set this option to true to use the prepareStatementStrategy with named parameters.
                // This allows to define queries with named placeholders, and use headers with the dynamic values for the query placeholders.
                .to("jdbc:target?useHeadersAsParameters=true");
        }
    }

    /**
     * Transforms the data extracted from the source database.
     *
     * <p>The transformation adds the following fields to the source data:</p>
     * <ul>
     *   <li><b>archiving_time</b>: The current timestamp when the data is processed.</li>
     *   <li><b>year</b>: The year context for the data.</li>
     * </ul>
     *
     * @param exchange the {@link Exchange} containing the extracted data in its message body.
     */
    @SuppressWarnings("unchecked")
    private void transform(Exchange exchange) {
        Map<String, Object> sourceData = exchange.getIn().getBody(Map.class);
        log.info("-> Extract applicant: {}", sourceData.get("id"));
        sourceData.put("archiving_time", LocalDateTime.now());
        sourceData.put("year", year);
    }
}
