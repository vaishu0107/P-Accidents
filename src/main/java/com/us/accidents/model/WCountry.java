package com.us.accidents.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class WCountry {

    private String name;
    private String code;

    private String capital;

    private String province;

    private int area;

    private int population;

    public WCountry(){
        super();
        name = "";
        code = "";
        capital = "";
        province = "";
        area = 0;
        population = 0;
    }
    public WCountry(String name, String code, String capital, String province, int area, int population) {
        this.name = name;
        this.code = code;
        this.capital = capital;
        this.province = province;
        this.area = area;
        this.population = population;
    }

    @Override
    public String toString(){
        return "Name " + name
                + " Code: " + code
                + " Capital: " + capital
                + " Province: " + province
                + " Area: " + area
                + " Population: " + population;
    }

}
