package io.crunch.route;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.inject.Inject;

import java.sql.SQLException;

public class BaseRouteTest {

    @Inject
    @DataSource("target")
    AgroalDataSource targetDatasource;

    @Inject
    @DataSource("source")
    AgroalDataSource sourceDatasource;

    int getQueryRowCount(String query, AgroalDataSource dataSource) throws SQLException {
        try (var statement = dataSource.getConnection().createStatement();
             var resultSet = statement.executeQuery(query)) {
            int count = 0;
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            return count;
        }
    }
}
