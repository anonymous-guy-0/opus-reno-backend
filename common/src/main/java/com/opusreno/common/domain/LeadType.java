package com.opusreno.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeadType {
    HOMEOWNER("homeowner"),
    ARCHITECT("architect");


    private final String wire;

    LeadType(String wire) { this.wire = wire; }

    @JsonValue
    public String wire() { return wire; }

    @JsonCreator
    public static LeadType fromWire(String wire) {
        for (LeadType t : values()) {
            if (t.wire.equals(wire)) return t;
        }
        throw new IllegalArgumentException("unknown LeadType wire value: " + wire);
    }
}
