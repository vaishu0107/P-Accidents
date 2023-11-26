package com.us.accidents.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ComputedIndices {
    private Float metric;
    private Integer indexValue;

    public ComputedIndices(){
        super();
        metric = null;
        indexValue = null;
    }
    public ComputedIndices(Float metric, Integer indexValue) {
        this.metric = metric;
        this.indexValue = indexValue;
    }
}

