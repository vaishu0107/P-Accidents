package com.us.accidents.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class YearPojo {
    private Integer year;

    public YearPojo(){
        super();
        year = null;
    }
    public YearPojo(Integer year) {
        this.year = year;
    }
}
