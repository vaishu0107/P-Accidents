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
        String sql = "SELECT ACC_Month as indexValue,\n" +
                "       AVG(Sum_Density) as metric\n" +
                "FROM\n" +
                "  (SELECT SUM(County_Density) Sum_Density,\n" +
                "          ACC_Year,\n" +
                "          ACC_Month\n" +
                "   FROM\n" +
                "     (SELECT County_ID,\n" +
                "             COUNT(Location_ID)/AVG(County_Area) AS County_Density,\n" +
                "             ACC_Year,\n" +
                "             ACC_Month\n" +
                "      FROM\n" +
                "        (SELECT CL.County_ID,\n" +
                "                CL.State_Name,\n" +
                "                CL.County_Area,\n" +
                "                Acc.Location_ID,\n" +
                "                EXTRACT(YEAR\n" +
                "                        FROM Start_Time) AS ACC_Year,\n" +
                "                EXTRACT(MONTH\n" +
                "                        FROM Start_Time) AS ACC_Month\n" +
                "         FROM\n" +
                "           (SELECT County_ID,\n" +
                "                   State_Name,\n" +
                "                   County_Area,\n" +
                "                   Location_ID\n" +
                "            FROM CountyArea\n" +
                "            JOIN AccidentLocation ON AccidentLocation.County = CountyArea.County_ID) CL\n" +
                "         JOIN \"VKALVA\".Accident Acc ON CL.Location_ID = Acc.Accident_ID\n" +
                "         AND State_Name = '"+stateName+"')\n" +
                "      GROUP BY ACC_Month,\n" +
                "               ACC_Year,\n" +
                "               County_ID\n" +
                "      ORDER BY Acc_Month ASC, ACC_Year ASC)\n" +
                "   GROUP BY County_ID,\n" +
                "            ACC_Year,\n" +
                "            ACC_Month\n" +
                "   ORDER BY Acc_YEAR ASC, ACC_Month ASC)\n" +
                "GROUP BY ACC_Month\n" +
                "ORDER BY ACC_Month ASC";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ComputedIndices.class));
    }


    public List<ComputedIndices> getTrafficIndices(Integer year) {
        String sql = "WITH Severity_Duration AS\n" +
                "  (SELECT Accident_ID,\n" +
                "          Severity,\n" +
                "          Start_Time\n" +
                "   FROM \"VKALVA\".Accident)\n" +
                "SELECT EXTRACT(MONTH\n" +
                "               FROM SD.Start_Time) AS indexValue,\n" +
                "       AVG(SD.Severity * (EXTRACT(DAY\n" +
                "                                  FROM (A.End_Time - A.Start_Time))*3600*24 + EXTRACT(HOUR\n" +
                "                                                                                      FROM (A.End_Time - A.Start_Time))*3600 + EXTRACT(MINUTE\n" +
                "                                                                                                                                       FROM (A.End_Time - A.Start_Time))*60 + EXTRACT(SECOND\n" +
                "                                                                                                                                                                                      FROM (A.End_Time - A.Start_Time)))) AS metric\n" +
                "FROM Severity_Duration SD\n" +
                "JOIN \"VKALVA\".Accident A ON A.Accident_ID = SD.Accident_ID\n" +
                "WHERE EXTRACT(YEAR\n" +
                "              FROM SD.Start_Time) = " + year + "\n" +
                "GROUP BY EXTRACT(MONTH\n" +
                "                 FROM SD.Start_Time)\n" +
                "ORDER BY indexValue ASC";
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
        String sql = " WITH Duration AS (\n" +
                "    SELECT\n" +
                "        A.Location_ID,\n" +
                "        A.Weather_ID,\n" +
                "        A.End_Time - A.Start_Time AS Duration,\n" +
                "        A.Start_Time\n" +
                "    FROM\n" +
                "        \"VKALVA\".Accident A\n" +
                "    WHERE\n" +
                "        A.Start_Time >= TO_TIMESTAMP('2016-01-01', 'YYYY-MM-DD') AND\n" +
                "        A.Start_Time < TO_TIMESTAMP('2024-01-01', 'YYYY-MM-DD') \n" +
                "),\n" +
                "AllHours AS (\n" +
                "    SELECT LEVEL - 1 AS HourNumber\n" +
                "    FROM dual\n" +
                "    CONNECT BY LEVEL <= 24\n" +
                ")\n" +
                "SELECT ah.HourNumber AS indexValue, \n" +
                "    AVG(\n" +
                "        COALESCE(\n" +
                "            EXTRACT(DAY FROM D.Duration) * 24 +\n" +
                "            EXTRACT(HOUR FROM D.Duration) +\n" +
                "            EXTRACT(MINUTE FROM D.Duration) / 60 +\n" +
                "            EXTRACT(SECOND FROM D.Duration) / 3600,\n" +
                "            0\n" +
                "        )\n" +
                "    ) AS metric\n" +
                "FROM AllHours ah\n" +
                "CROSS JOIN StateArea sa\n" +
                "LEFT JOIN Duration D ON EXTRACT(HOUR FROM D.Start_Time) = ah.HourNumber\n" +
                "LEFT JOIN AccidentLocation al ON D.Location_ID = al.Location_ID\n" +
                "LEFT JOIN CountyArea ca ON al.County = ca.County_ID\n" +
                "GROUP BY ah.HourNumber\n" +
                "ORDER BY ah.HourNumber ";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ComputedIndices.class));
    }

    public List<ComputedIndices> getAccidentFactorIndices(String stateName) {
        String sql = " SELECT (AVG(Severity)*COUNT(Accident_ID))/((SUM(POPULATION)/COUNT(Population))*( SUM(State_Area)/COUNT(State_Area))) * 10000000 AS metric,AC_Year AS indexValue FROM (SELECT Severity,Accident_ID, AC_Year FROM \n" +
                "(SELECT Severity, Accident_ID, EXTRACT(YEAR FROM Start_Time) AS AC_Year FROM \"VKALVA\".Accident ) \"AC\") \"NUME\" JOIN (SELECT SD.State_Area, SD.Year, SD.Population, SD.State_Name, LocaD.Location_ID\n" +
                "FROM (\n" +
                "    SELECT SA.State_Area, SP.Year, SP.Population, SA.State_Name  \n" +
                "    FROM StateArea SA \n" +
                "    JOIN \"VKALVA\".StatePopulation SP ON SA.State_Name = SP.State_Name\n" +
                ") SD\n" +
                "JOIN (\n" +
                "    SELECT CA.State_Name, AL.Location_ID \n" +
                "    FROM AccidentLocation AL\n" +
                "    JOIN CountyArea CA ON CA.County_id = AL.County\n" +
                ") LocaD ON SD.State_Name = LocaD.State_Name) \"DENO\" ON DENO.Location_ID = NUME.Accident_ID AND Year = AC_Year AND State_Name = '" + stateName + "'  GROUP BY AC_Year ORDER BY AC_Year ASC ";
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
