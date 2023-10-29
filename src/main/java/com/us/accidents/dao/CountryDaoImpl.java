package com.us.accidents.dao;

import com.google.gson.Gson;
import com.us.accidents.model.LoginCreds;
import com.us.accidents.model.WCountry;
import org.springframework.beans.factory.annotation.Autowired;
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
        String hashSql = " SELECT PASSWORD FROM ACCIDENTS_USERS WHERE NAME = '" + username + "' ";
        return jdbcTemplate.queryForObject(hashSql, String.class);
    }

    public List<WCountry> getCountryInfo(String countryName) {
        String sql = " SELECT * FROM WCOUNTRY WHERE NAME = '" + countryName + "' ";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(WCountry.class));
    }
}
