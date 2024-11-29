package io.crunch.route;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.apache.camel.model.RouteDefinition;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@QuarkusTest
@TestProfile(ApplicantMessageRouteTest.ApplicantMessageRouteProfile.class)
class ApplicantMessageRouteTest extends BaseRouteTest {

    @Inject
    ApplicantMessageRoute applicantMessageRoute;

    @Test
    void whenApplicantMessageRouteIsCalledMessagesAreTransformedToTarget() throws SQLException {
        assertThat(applicantMessageRoute.getRouteCollection().getRoutes()
            .stream()
            .map(RouteDefinition::getEndpointUrl)
            .anyMatch(url -> url.equals("timer://load-applicant-message?delay=-1&repeatCount=1"))).isTrue();

        var sourceQuery = "SELECT count(m.id) FROM APPLICANT_MESSAGE m, APPLICANT a, UNIVERSITY u WHERE a.id = m.a_id AND a.u_id = u.id";
        var targetQuery = "SELECT count(id) FROM APPLICANT_MESSAGE where year = " + getYear();
        var numberOfMessages = getQueryRowCount(sourceQuery, sourceDatasource);
        assertThat(numberOfMessages).isPositive();
        await()
            .atMost(Durations.ONE_MINUTE)
            .pollInterval(Durations.ONE_SECOND)
            .untilAsserted(() -> assertThat(getQueryRowCount(targetQuery, targetDatasource)).isEqualTo(numberOfMessages));
    }

    public static class ApplicantMessageRouteProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "app.archive.command", "load-applicant-message",
                    "app.archive.year", getYear());
        }
    }
}
