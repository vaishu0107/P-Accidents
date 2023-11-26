package com.us.accidents.controller;

import com.us.accidents.helper.CountryHelper;
import com.us.accidents.model.LoginCreds;
import com.us.accidents.model.ComputedIndices;
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
import java.util.List;

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
        if(actualPasswordHash.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid Login Credentials");
        }
        else if(actualPasswordHash.equals(hashedString)) {
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



    //query 1
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/getAccidentDensities", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComputedIndices>> getAccidentDensities(@RequestBody String stateName)
    {
        List<ComputedIndices> accidentDensityInfo = List.of(new ComputedIndices(1.0F, 1),
                new ComputedIndices(3.0F, 2),new ComputedIndices(3.0F, 3));
        //List<ComputedIndices> accidentDensityInfo = countryHelper.getAccidentDensities(stateName);
        return ResponseEntity.ok(accidentDensityInfo);
    }

    //query 2
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/getTrafficSeverity", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComputedIndices>> getTrafficSeverity(@RequestBody String year)
    {
        List<ComputedIndices> trafficSeverityInfo = List.of(new ComputedIndices(1.0F, 1),
                new ComputedIndices(3.0F, 2),new ComputedIndices(3.0F, 3));
        //List<ComputedIndices> trafficSeverityInfo = countryHelper.getTrafficSeverity(year);
        return ResponseEntity.ok(trafficSeverityInfo);
    }

    //query 3
    @CrossOrigin(origins = "*")
    @GetMapping(path = "/getSafetyIndices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComputedIndices>> getSafetyIndices()
    {
        List<ComputedIndices> safetyIndicesInfo = countryHelper.getSafetyIndices();
        return ResponseEntity.ok(safetyIndicesInfo);
    }

    //query 4 // hour and avg road block index
    @CrossOrigin(origins = "*")
    @GetMapping(path = "/getRoadBlockIndices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComputedIndices>> getRoadBlockIndices()
    {
        List<ComputedIndices> roadBlockIndices = List.of(new ComputedIndices(1.0F, 1),
                new ComputedIndices(3.0F, 2),new ComputedIndices(3.0F, 3));
        //List<ComputedIndices> roadBlockIndices = countryHelper.getRoadBlockIndices();
        return ResponseEntity.ok(roadBlockIndices);
    }

    //query 5
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/getAccidentFactorIndices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComputedIndices>> getAccidentFactorIndices(@RequestBody String stateName)
    {
        List<ComputedIndices> accidentFactorIndices = List.of(new ComputedIndices(1.0F, 1),
                new ComputedIndices(3.0F, 2),new ComputedIndices(3.0F, 3));
        //List<ComputedIndices> accidentFactorIndices = countryHelper.getAccidentFactorIndices(stateName);
        return ResponseEntity.ok(accidentFactorIndices);
    }
}
