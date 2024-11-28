package io.crunch.route;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;

import java.time.LocalDateTime;
import java.util.Map;

@ApplicationScoped
public class ApplicantRoute extends RouteBase {

    private static final String SOURCE_QUERY_TEMPLATE = "applicant-source-query.sql";

    private static final String TARGET_QUERY_TEMPLATE = "applicant-target-query.sql";

    @Override
    public void configure() throws Exception {
        if(isTriggered("load-applicant")) {
            from("timer://load-applicant?delay=-1&repeatCount=1")
                .routeId("etl-applicant")
                .setBody()
                    .simple(getTemplate(SOURCE_QUERY_TEMPLATE))
                    .log("-> Extracting data from Source Database {{quarkus.datasource.source.jdbc.url}}, SQL command: ${body}")
                .to("jdbc:source")
                .split(body())
                .process(this::transform)
                    .log("-> Transforming applicant: ${body[app_id]}")
                    .setHeaders(getExpressions(TARGET_QUERY_TEMPLATE))
                    .setBody(constant(getTemplate(TARGET_QUERY_TEMPLATE)))
                    .log("-> Loading transformed data in target database, SQL command: ${body}")
                .to("jdbc:target?useHeadersAsParameters=true");
        }
    }

    @SuppressWarnings("unchecked")
    private void transform(Exchange exchange) {
        Map<String, Object> sourceData = exchange.getIn().getBody(Map.class);
        log.info("-> Extract applicant: {}", sourceData.get("id"));
        sourceData.put("archiving_time", LocalDateTime.now());
        sourceData.put("year", year);
    }
}
