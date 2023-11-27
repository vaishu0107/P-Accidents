package com.us.accidents.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class StatePojo {
    private String stateName;

    public StatePojo(){
        super();
        stateName = "";
    }
    public StatePojo(String stateName) {
        this.stateName = stateName;
    }
}
