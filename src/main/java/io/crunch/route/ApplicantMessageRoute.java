package io.crunch.route;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;

import java.time.LocalDateTime;
import java.util.Map;

@ApplicationScoped
public class ApplicantMessageRoute extends RouteBase {

    private static final String SOURCE_QUERY_TEMPLATE = "applicant-message-source-query.sql";

    private static final String TARGET_QUERY_TEMPLATE = "applicant-message-target-query.sql";

    @Override
    public void configure() throws Exception {
        if(isTriggered("load-applicant-message")) {
            from("timer://load-applicant-message?delay=-1&repeatCount=1")
                .routeId("applicant-message-route")
                .setBody()
                    .simple(getTemplate(SOURCE_QUERY_TEMPLATE))
                    .log("-> Extracting data from Source Database {{quarkus.datasource.source.jdbc.url}}, SQL command: ${body}")
                .to("jdbc:source")
                .split(body())
                .process(this::transform)
                    .log("-> Transforming message ${body[id]} of applicant: ${body[can_id]}")
                    .setHeaders(getExpressions(TARGET_QUERY_TEMPLATE))
                    .setBody(constant(getTemplate(TARGET_QUERY_TEMPLATE)))
                    .log("-> Loading transformed data in target database, SQL command: ${body}")
                .to("jdbc:target?useHeadersAsParameters=true");
        }
    }

    @SuppressWarnings("unchecked")
    private void transform(Exchange exchange) {
        Map<String, Object> sourceData = exchange.getIn().getBody(Map.class);
        log.info("-> Extract applicant message: {}", sourceData.get("id"));
        sourceData.put("archiving_time", LocalDateTime.now());
        sourceData.put("year", year);
        sourceData.put("app_year", year);
    }
}
