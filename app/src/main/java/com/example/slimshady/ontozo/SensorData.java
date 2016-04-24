package com.example.slimshady.ontozo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SensorData {

    public final double humidity;
    public final double temperature;
    public final double light;

    @JsonCreator
    public SensorData(@JsonProperty("h") long humidity, @JsonProperty("t") double temperature, @JsonProperty("l") long light){
        this.humidity = humidity;
        this.temperature = temperature;
        this.light = light;
    }
}
