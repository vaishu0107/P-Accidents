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
        String resultantPassword = jdbcTemplate.queryForObject(hashSql, String.class);
        return resultantPassword;
    }

    public List<WCountry> getCountryInfo(String countryName) {
        String sql = " SELECT * FROM WCOUNTRY WHERE NAME = '" + countryName + "' ";
        //String sql = " SELECT * FROM WCOUNTRY ";
        List<WCountry> countryDetails =  jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(WCountry.class));
        return countryDetails;
        // ArrayList countryList = new ArrayList<>((Collection) new WCountry("India","IND","Delhi","India",500000,100000000));
        // countryList.stream().filter(country -> country.)
       // return new WCountry("India", "IND", "Delhi", "India", 500000, 100000000);

    }
}
