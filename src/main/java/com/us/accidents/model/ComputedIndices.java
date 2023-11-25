package com.us.accidents.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ComputedIndices {
    private String metric;
    private String indexValue;

    public ComputedIndices(){
        super();
        metric = "";
        indexValue = "";
    }
    public ComputedIndices(String metric, String indexValue) {
        this.metric = metric;
        this.indexValue = indexValue;
    }
}

