package com.us.accidents.helper;

import com.us.accidents.dao.CountryDaoImpl;
import com.us.accidents.model.WCountry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class CountryHelper {
    public static final Logger logger = LoggerFactory.getLogger(CountryHelper.class);

    @Autowired
    public CountryDaoImpl countryDaoImpl;

    public String getHashedPassword(String username){
        String hashedPassword = countryDaoImpl.getHashedPassword(username);
        return hashedPassword;
    }

    public List<WCountry> get(String countryName){
        logger.info("GetCountryDetailsHelper: Starts");
        return countryDaoImpl.getCountryInfo(countryName);
    }
}
