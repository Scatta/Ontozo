package com.example.slimshady.ontozo;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CommandCode {
    ON ( 1 ),
    OFF ( 2 );

    private int value;

    CommandCode(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
