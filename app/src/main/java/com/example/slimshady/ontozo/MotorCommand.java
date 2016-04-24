package com.example.slimshady.ontozo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class MotorCommand {
    @JsonProperty("m")
    public final CommandCode commandCode;

    @JsonCreator
    public MotorCommand(@JsonProperty("m") CommandCode m){
        this.commandCode = m;
    }

    public String toJsonString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
