package com.internship.rushhour.domain.report.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.report.models.client.ExpensesReport;
import com.internship.rushhour.domain.report.models.client.FavoriteActivitiesReport;
import com.internship.rushhour.domain.report.models.client.FavoriteProviderReport;
import com.internship.rushhour.domain.report.models.employee.availability.Availability;
import com.internship.rushhour.domain.report.models.employee.availability.AvailabilityPerWeek;
import com.internship.rushhour.domain.report.models.employee.availability.AvailabilityPerWeekRequest;
import com.internship.rushhour.domain.report.models.employee.availability.AvailabilityRequest;
import com.internship.rushhour.domain.report.models.employee.mostandleastbooked.MostBookedTime;
import com.internship.rushhour.domain.report.models.employee.topten.TopTenClients;
import com.internship.rushhour.domain.report.models.employee.topten.TopTenRequest;
import com.internship.rushhour.domain.report.models.provider.AvailabilityReport;
import com.internship.rushhour.domain.report.models.provider.IncomeReport;
import com.internship.rushhour.domain.report.models.provider.ProductivityReport;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class ReportRepository {

    @PersistenceContext
    EntityManager entityManager;

    // PROVIDER Reports

    public List<ProductivityReport> getProductivityReport(Long providerId, Integer year) {
        String sql = """
                    SELECT
                      account.name AS "employee"
                     ,COUNT(*) AS "appointments"
                     ,SUM(TIMESTAMPDIFF(MINUTE, start_time, end_date))/60 AS "hours"
                     ,SUM(appointment.price) AS "productivity"
                    FROM appointment INNER JOIN employee ON appointment.employee = employee.id
                     INNER JOIN account ON employee.account = account.id
                     INNER JOIN provider ON employee.provider = provider.id
                    WHERE employee.provider = ?1
                      AND YEAR(start_time) = ?2
                    GROUP BY employee.id
                    ORDER BY productivity DESC
                    """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, providerId);
        query.setParameter(2, year);

        return getQueryResult(query, ProductivityReport.class);
    }

    public List<AvailabilityReport> getAvailabilityReport(Long providerId, Integer weekNumber) {
        String sql = """
                    SELECT
                      account.name AS "employee"
                     ,DATE_FORMAT(working_hours.hour, '%H:%i') as "hour"
                     ,SUM(CASE WHEN DAYOFWEEK(start_time) = 2 AND HOUR(working_hours.hour) = HOUR(start_time) THEN 1 ELSE 0 END) AS "monday"
                     ,SUM(CASE WHEN DAYOFWEEK(start_time) = 3 AND HOUR(working_hours.hour) = HOUR(start_time) THEN 1 ELSE 0 END) AS "tuesday"
                     ,SUM(CASE WHEN DAYOFWEEK(start_time) = 4 AND HOUR(working_hours.hour) = HOUR(start_time) THEN 1 ELSE 0 END) AS "wednesday"
                     ,SUM(CASE WHEN DAYOFWEEK(start_time) = 5 AND HOUR(working_hours.hour) = HOUR(start_time) THEN 1 ELSE 0 END) AS "thursday"
                     ,SUM(CASE WHEN DAYOFWEEK(start_time) = 6 AND HOUR(working_hours.hour) = HOUR(start_time) THEN 1 ELSE 0 END) AS "friday"
                     ,SUM(CASE WHEN DAYOFWEEK(start_time) = 7 AND HOUR(working_hours.hour) = HOUR(start_time) THEN 1 ELSE 0 END) AS "saturday"
                     ,SUM(CASE WHEN DAYOFWEEK(start_time) = 1 AND HOUR(working_hours.hour) = HOUR(start_time) THEN 1 ELSE 0 END) AS "sunday"
                    FROM appointment
                      CROSS JOIN working_hours
                      INNER JOIN employee ON appointment.employee = employee.id
                      INNER JOIN account ON employee.account = account.id
                      INNER JOIN provider ON employee.provider = provider.id
                    WHERE provider.id = ?1 AND WEEK(start_time) = ?2
                    GROUP BY hour, employee.id
                    ORDER BY employee.id, hour
                    """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, providerId);
        query.setParameter(2, weekNumber);

        return getQueryResult(query, AvailabilityReport.class);
    }

    public List<IncomeReport> getIncomeReport(Long providerId, Integer year) {
        String sql = """
                    SELECT
                       provider.name AS "provider"
                      ,SUM(CASE WHEN MONTH(start_time) = 1 THEN price ELSE 0 END) AS "january"
                      ,SUM(CASE WHEN MONTH(start_time) = 2 THEN price ELSE 0 END) AS "february"
                      ,SUM(CASE WHEN MONTH(start_time) = 3 THEN price ELSE 0 END) AS "march"
                      ,SUM(CASE WHEN QUARTER(start_time) = 1 THEN price ELSE 0 END) AS "q1"
                      ,SUM(CASE WHEN MONTH(start_time) = 4 THEN price ELSE 0 END) AS "april"
                      ,SUM(CASE WHEN MONTH(start_time) = 5 THEN price ELSE 0 END) AS "may"
                      ,SUM(CASE WHEN MONTH(start_time) = 6 THEN price ELSE 0 END) AS "june"
                      ,SUM(CASE WHEN QUARTER(start_time) = 2 THEN price ELSE 0 END) AS "q2"
                      ,SUM(CASE WHEN MONTH(start_time) = 7 THEN price ELSE 0 END) AS "july"
                      ,SUM(CASE WHEN MONTH(start_time) = 8 THEN price ELSE 0 END) AS "august"
                      ,SUM(CASE WHEN MONTH(start_time) = 9 THEN price ELSE 0 END) AS "september"
                      ,SUM(CASE WHEN QUARTER(start_time) = 3 THEN price ELSE 0 END) AS "q3"
                      ,SUM(CASE WHEN MONTH(start_time) = 10 THEN price ELSE 0 END) AS "october"
                      ,SUM(CASE WHEN MONTH(start_time) = 11 THEN price ELSE 0 END) AS "november"
                      ,SUM(CASE WHEN MONTH(start_time) = 12 THEN price ELSE 0 END) AS "december"
                      ,SUM(CASE WHEN QUARTER(start_time) = 4 THEN price ELSE 0 END) AS "q4"
                      ,SUM(price) AS "yearTotal"
                    FROM appointment
                      INNER JOIN employee ON appointment.employee = employee.id
                      INNER JOIN provider ON employee.provider = provider.id
                    WHERE employee.provider = ?1
                       AND YEAR(DATE(start_time)) = ?2
                    GROUP BY provider.name
                    """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, providerId);
        query.setParameter(2, year);

        return getQueryResult(query, IncomeReport.class);
    }

    private <T> List<T> getQueryResult(Query query, Class<T> classType){

        NativeQueryImpl<?> nativeQuery = (NativeQueryImpl<?>) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<?> result = nativeQuery.getResultList();

        List<T> report = new ArrayList<>();
        IntStream.range(0, result.size())
                .forEach(i -> report.add(new ObjectMapper().convertValue(result.get(i), classType)));

        return report;
    }

    //EMPLOYEE Reports

    public List<TopTenClients> getTopTenClientsPerMoneyOrTimeByYear(Long employeeId,
                                                                    String sort_time_money,
                                                                    TopTenRequest topTenRequest,
                                                                    String addToQuery) {

        String sql = "SELECT client.id AS \"Client id\",\n" +
                "       account.name AS \"Client name\",\n" +
                "       SUM(appointment.price) AS \"Total price\",\n" +
                "       ROUND(SUM(TIMESTAMPDIFF(MINUTE, start_time, end_date)/60),2) AS \"Total hours\"\n" +
                "FROM appointment\n" +
                "INNER JOIN client ON appointment.client = client.id\n" +
                "INNER JOIN account ON client.account = account.id\n" +
                "INNER JOIN employee ON employee.id=appointment.employee\n" +
                "WHERE appointment.employee = ?1 AND YEAR(start_time) = ?2 \n" +
                addToQuery +
                "\n GROUP BY YEAR(start_time), client.id\n" +
                sort_time_money +
                "LIMIT 10";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, employeeId);
        query.setParameter(2, topTenRequest.getYear());

        List<Object[]> objects = query.getResultList();

        return objects.stream()
                .map(x -> new TopTenClients(Long.parseLong(String.valueOf(x[0])),
                        x[1].toString(),
                        Double.parseDouble(String.valueOf(x[2])),
                        Double.parseDouble(String.valueOf(x[3]))))
                .collect(Collectors.toList());
    }

    public List<MostBookedTime> getMostAndLeastBookedTime(Long employeeId,
                                                          String weekOrMonth,
                                                          String num,
                                                          String year) {

        String sql = "SELECT wh.hour,\n" +
                "\t\tCASE WHEN a.NoA is null THEN 0 \n" +
                "        ELSE a.NoA \n" +
                "\t\tEND AS Num_of_appointments\n" +
                "FROM working_hours wh\n" +
                "LEFT JOIN \n" +
                "(SELECT\n" +
                "time(start_time) as wh, \n" +
                "COUNT(time(start_time)) as NoA\n" +
                "FROM appointment\n" +
                "WHERE appointment.employee = ?1 AND YEAR(start_time) = ?2 AND " + weekOrMonth + num +
                "\n GROUP BY time(start_time)) a ON a.wh=wh.hour\n" +
                "ORDER BY Num_of_appointments DESC";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, employeeId);
        query.setParameter(2, year);
        List<Object[]> objects = query.getResultList();

        return objects.stream()
                .map(x -> new MostBookedTime(x[0].toString(),
                        Long.parseLong(String.valueOf(x[1]))))
                .collect(Collectors.toList());
    }

    public List<Availability> getAvailability(Long employeeId, AvailabilityRequest availabilityRequest) {

        String sql = """
                    SELECT wh.hour,
                        CASE WHEN a.NoA is null THEN "available"
                        ELSE "BOOKED"
                        END AS booked_or_not
                    FROM working_hours wh
                    LEFT JOIN
                    (SELECT
                    time(start_time) as wh,
                    (time(start_time)) as NoA
                    FROM appointment
                    WHERE date(appointment.start_time) = ?1 AND appointment.employee = ?2
                    GROUP BY time(start_time)) a ON a.wh=wh.hour
                    ORDER BY hour
                    """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, availabilityRequest.getDate());
        query.setParameter(2, employeeId);
        List<Object[]> objects = query.getResultList();

        return objects.stream()
                .map(x -> new Availability(x[0].toString(),
                        x[1].toString()))
                .collect(Collectors.toList());
    }

    public List<AvailabilityPerWeek> getAvailabilityPerWeek(Long employeeId, AvailabilityPerWeekRequest availabilityPerWeekRequest) {

        String sql = """
                SELECT
                   DATE_FORMAT(working_hours.hour, '%H:%i') as Hour,
                   CASE WHEN DAYOFWEEK(start_time) = 2 AND HOUR(working_hours.hour) = HOUR(start_time) THEN "BOOKED" ELSE "available" END AS "Monday",
                   CASE WHEN DAYOFWEEK(start_time) = 3 AND HOUR(working_hours.hour) = HOUR(start_time) THEN "BOOKED" ELSE "available" END AS "Tuesday",
                   CASE WHEN DAYOFWEEK(start_time) = 4 AND HOUR(working_hours.hour) = HOUR(start_time) THEN "BOOKED" ELSE "available" END AS "Wednesday",
                   CASE WHEN DAYOFWEEK(start_time) = 5 AND HOUR(working_hours.hour) = HOUR(start_time) THEN "BOOKED" ELSE "available" END AS "Thursday",
                   CASE WHEN DAYOFWEEK(start_time) = 6 AND HOUR(working_hours.hour) = HOUR(start_time) THEN "BOOKED" ELSE "available" END AS "Friday"
                FROM appointment
                   CROSS JOIN working_hours
                   INNER JOIN employee ON appointment.employee = employee.id
                WHERE employee.id = ?1 AND WEEK(start_time) = ?2 AND YEAR(start_time) = ?3
                GROUP BY DATE_FORMAT(working_hours.hour, '%H:%i'), Monday, Tuesday, Wednesday, Thursday, Friday
                ORDER BY Hour
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, employeeId);
        query.setParameter(2, availabilityPerWeekRequest.getWeek());
        query.setParameter(3, availabilityPerWeekRequest.getYear());
        List<Object[]> objects = query.getResultList();

        return objects.stream()
                .map(x -> new AvailabilityPerWeek(x[0].toString(),
                        x[1].toString(),
                        x[2].toString(),
                        x[3].toString(),
                        x[4].toString(),
                        x[5].toString()))
                .collect(Collectors.toList());
    }


    //CLIENT Reports

    public List<FavoriteProviderReport> getFavoriteProviders(Client client, String sqlForLast) {
        String sql = """
                SELECT 
                provider.name AS "provider",
                SUM(appointment.price) AS "appPrice",
                COUNT(*) AS "numOfApp"
                FROM appointment 
                INNER JOIN employee ON appointment.employee = employee.id
                INNER JOIN provider ON employee.provider = provider.id
                WHERE appointment.client = ?1
                """
                + sqlForLast +
                """
                GROUP BY provider.id
                ORDER BY SUM(appointment.price) DESC 
                LIMIT 3;
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, client);

        List<Object[]> objects = query.getResultList();

        return objects.stream()
                .map(x -> new FavoriteProviderReport(x[0].toString(),
                        Double.parseDouble(String.valueOf(x[1])),
                        Long.parseLong(String.valueOf(x[2]))))
                .collect(Collectors.toList());


    }

    public List<FavoriteActivitiesReport> getFavoriteActivities(Client client, String sqlForLast){
        String sql = """
                SELECT 
                provider.name AS "provider",
                activity.name AS "name",
                COUNT(*) AS "numOfActivities"
                FROM appointment_activities
                INNER JOIN appointment ON appointment_activities.appointment_id = appointment.id
                INNER JOIN activity ON appointment_activities.activity_id = activity.id
                INNER JOIN provider ON activity.provider = provider.id
                WHERE appointment.client = ?1 
                """
                + sqlForLast +
                """
                GROUP BY  provider.name, activity.name 
                ORDER BY COUNT(*) DESC 
                LIMIT 5;
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, client);

        List<Object[]> objects = query.getResultList();

        return objects.stream()
                .map(x -> new FavoriteActivitiesReport(x[0].toString(),
                        x[1].toString(),
                        Long.parseLong(String.valueOf(x[2]))))
                .collect(Collectors.toList());
    }

    public List<ExpensesReport> getExpensesReports(Client client, Long year, String sql){
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, client);
        query.setParameter(2, year);

        List<Object[]> objects = query.getResultList();

        return objects.stream()
                .map(x -> new ExpensesReport(x[0].toString(),
                        Double.parseDouble(String.valueOf(x[1]))))
                .collect(Collectors.toList());
    }

}
