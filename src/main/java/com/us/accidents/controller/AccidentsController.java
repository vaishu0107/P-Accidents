package com.us.accidents.controller;

import com.us.accidents.helper.CountryHelper;
import com.us.accidents.model.LoginCreds;
import com.us.accidents.model.WCountry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static java.awt.SystemColor.text;

@RestController
@RequestMapping("/v1")
public class AccidentsController {
    public static final Logger logger = LoggerFactory.getLogger(AccidentsController.class);
    Gson gson = new Gson();

    @Autowired
    private CountryHelper countryHelper;

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginCreds userCreds) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytesArray = digest.digest(userCreds.getPassword().getBytes(StandardCharsets.UTF_8));
        String hashedString = bytesToHex(hashBytesArray);
        String actualPasswordHash = countryHelper.getHashedPassword(userCreds.getUsername());

        if(actualPasswordHash.equals(hashedString)) {
            return ResponseEntity.ok("Welcome!");
        } else {
            return ResponseEntity.badRequest().body("Invalid Login Credentials");
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    @CrossOrigin(origins = "*")
    @GetMapping(path = "/getCountry", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WCountry>> getCountry(@RequestParam(name = "country") String countryName)
    {
        logger.info("AccidentsController request getCountry: for " + countryName);
        List<WCountry> countryInfo = countryHelper.get(countryName);
        logger.info("AccidentsController request getCountry GOT for" + countryName + " " + gson.toJson(countryInfo));
        return ResponseEntity.ok(countryInfo);
    }


}
