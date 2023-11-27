package com.us.accidents.dao;

import com.google.gson.Gson;
import com.us.accidents.model.ComputedIndices;
import com.us.accidents.model.WCountry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CountryDaoImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    Gson gson = new Gson();

    public String getHashedPassword(String username){
        String hashSql = " SELECT PASSWORD FROM \"VKALVA\".ACCIDENTS_USERS WHERE NAME = '" + username + "' ";
        try {
            return jdbcTemplate.queryForObject(hashSql, String.class);
        } catch (EmptyResultDataAccessException e) {
            return "";
        }
    }

    public List<WCountry> getCountryInfo(String countryName) {
        String sql = " SELECT * FROM WCOUNTRY WHERE NAME = '" + countryName + "' ";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(WCountry.class));
    }

    public List<ComputedIndices> getAccidentDensitiesInfo(String stateName) {
        String sql = " WITH YEAR_COUNT AS (SELECT COUNT(DISTINCT EXTRACT(year FROM A.Start_Time)) AS NUMBER_OF_YEARS \n" +
                "FROM ACCIDENTS) \n" +
                " \n" +
                "SELECT EXTRACT(month FROM A.Start_Time) AS MONTH_NAME, COUNT(*)/(S.State_Area * YEAR_COUNT.NUMBER_OF_YEARS) AS AVG_ACCIDENT_DENSITY FROM  \n" +
                "Accident A \n" +
                "JOIN AccidentLocation AL ON A.Location_ID = AL.Location_ID \n" +
                "JOIN CountyArea C ON C.County_ID = AL.County_ID \n" +
                "JOIN StateArea S ON S.State_Name = C. State_Name \n" +
                "CROSS JOIN YEAR_COUNT \n" +
                "WHERE S.State_Name = ‘" + stateName + "’ \n" +
                "GROUP BY EXTRACT(month FROM A.Start_Time) \n" +
                "ORDER BY MONTH_NAME ASC;  ";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ComputedIndices.class));
    }


    public List<ComputedIndices> getTrafficIndices(String year) {
        String sql = " WITH Severity_Duration AS (SELECT Accident_ID, (Severity*(End_Time -Start_Time)) AS severitymetric, EXTRACT(year FROM A.Start_Time) AS Year FROM ACCIDENT) \n" +
                " \n" +
                "SELECT EXTRACT(month FROM SD.Start_Time) AS MONTH_NAME, AVG(SD.severitymetric) AS Traffic_Severity \n" +
                "FROM Severity_Duration SD  \n" +
                "JOIN Accident A ON A.Weather_ID = SD.Weather_ID \n" +
                "WHERE Year = ‘" + year + "’ \n" +
                " GROUP BY EXTRACT(month FROM SD.Start_Time) \n" +
                "ORDER BY MONTH_NAME ASC;  ";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ComputedIndices.class));
    }

    public List<ComputedIndices> getSafetyIndicesInfo() {
        String sql = "WITH SafetyIndices AS (\n" +
                "    SELECT \n" +
                "        Weather_ID,\n" +
                "        (Visibility / (Wind_Speed + Precipitation + (Temperature * Temperature - 154 * Temperature + 5929))) AS Safety_Index\n" +
                "    FROM \n" +
                "        WEATHER\n" +
                "    WHERE \n" +
                "        (Wind_Speed + Precipitation + (Temperature * Temperature - 154 * Temperature + 5929) <> 0)\n" +
                ")\n" +
                "\n" +
                "SELECT \n" +
                "    EXTRACT(month FROM A.Start_Time) AS indexValue, \n" +
                "    AVG(SI.Safety_Index) AS metric\n" +
                "FROM \n" +
                "    \"VKALVA\".Accident A\n" +
                "JOIN \n" +
                "    SafetyIndices SI ON A.Weather_ID = SI.Weather_ID\n" +
                "GROUP BY \n" +
                "    EXTRACT(month FROM A.Start_Time)\n" +
                "ORDER BY \n" +
                "    indexValue ASC";

        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ComputedIndices.class));
    }


    public List<ComputedIndices> getRoadBlockIndices() {
        String sql = " WITH Durations AS (SELECT Weather_ID, (End_Time - Start_Time) AS Duration FROM WEATHER) \n" +
                " \n" +
                "SELECT EXTRACT(hour FROM A.Start_Time) AS Hour, AVG(D.Durations) AS Avg_Road_Block \n" +
                "FROM Durations D  \n" +
                "JOIN Accidents A ON A.Weather_ID = D.Weather_ID \n" +
                "GROUP BY EXTRACT(hour FROM A.Start_Time) \n" +
                "ORDER BY Hour ASC; ";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ComputedIndices.class));
    }


    public List<ComputedIndices> getAccidentFactorIndices(String stateName) {
        String sql = " SELECT  \n" +
                "    EXTRACT(year FROM A.Start_Time) AS Year, \n" +
                "    (SUM(A.Severity) * COUNT(*) / (S.State_Area * SP.Population)) AS Accidents_Factor \n" +
                "FROM  \n" +
                "    Accident A \n" +
                "JOIN  \n" +
                "    AccidentLocation AL ON A.Location_ID = AL.Location_ID \n" +
                "JOIN  \n" +
                "    CountyArea C ON C.County_ID = AL.County_ID \n" +
                "JOIN  \n" +
                "    StateArea S ON S.State_Name = C.State_Name \n" +
                "JOIN  \n" +
                "    StatePopulation SP ON S.State_Name = SP.State_Name AND EXTRACT(year FROM A.Start_Time) = SP.Year \n" +
                "WHERE  \n" +
                "    S.State_Name = '" + stateName + "' \n" +
                "GROUP BY  \n" +
                "    EXTRACT(year FROM A.Start_Time);  ";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ComputedIndices.class));
    }

    public Integer getTotalTuplesCount() {
        String hashSql = " WITH per_table_counts AS (\n" +
                "  SELECT COUNT(*) AS row_count FROM \"VKALVA\".ACCIDENT\n" +
                "  UNION ALL\n" +
                "  SELECT COUNT(*) FROM \"VKALVA\".StatePopulation\n" +
                "  UNION ALL\n" +
                "  SELECT COUNT(*) FROM AccidentLocation\n" +
                "  UNION ALL\n" +
                "  SELECT COUNT(*) FROM CountyArea\n" +
                "  UNION ALL\n" +
                "  SELECT COUNT(*) FROM StateArea\n" +
                "  UNION ALL\n" +
                "  SELECT COUNT(*) FROM WEATHER\n" +
                ")\n" +
                "SELECT SUM(row_count) AS total_rows\n" +
                "FROM per_table_counts ";
        try {
            return jdbcTemplate.queryForObject(hashSql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
