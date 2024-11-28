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

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestProfile(ApplicantRouteTest.ApplicantRouteProfile.class)
class ApplicantRouteTest extends BaseRouteTest {

    @Inject
    ApplicantRoute applicantRoute;

    @Test
    void whenApplicantRouteIsCalledApplicantsTransformedToTarget() throws SQLException {
        assertTrue(applicantRoute.getRouteCollection().getRoutes()
            .stream()
            .map(RouteDefinition::getEndpointUrl)
            .anyMatch(url -> url.equals("timer://load-applicant?delay=-1&repeatCount=1")));

        var sourceQuery = "SELECT count(a.id) FROM APPLICANT a, UNIVERSITY u WHERE a.u_id = u.id";
        var targetQuery = "SELECT count(id) FROM APPLICANT where year = " + getYear();
        var numberOfApplicants = getQueryRowCount(sourceQuery, sourceDatasource);
        assertTrue(numberOfApplicants > 0);
        await()
            .atMost(Durations.TEN_SECONDS)
            .pollInterval(Durations.ONE_SECOND)
            .untilAsserted(() -> assertEquals(numberOfApplicants, getQueryRowCount(targetQuery, targetDatasource)));
    }

    public static class ApplicantRouteProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
            "app.archive.command", "load-applicant",
            "app.archive.year", getYear());
        }
    }
}
