package com.opusreno.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeadScope {
    FULL_RENOVATION("fullRenovation"),
    KITCHEN_BATHROOM("kitchenBathroom"),
    CIVIL_STRUCTURAL("civilStructural"),
    INTERIOR_FITOUT("interiorFitout");

    private final String wire;

    LeadScope(String wire) { this.wire = wire; }

    @JsonValue
    public String wire() { return wire; }

    @JsonCreator
    public static LeadScope fromWire(String wire) {
        for (LeadScope s : values()) {
            if (s.wire.equals(wire)) return s;
        }
        throw new IllegalArgumentException("unknown LeadScope wire value: " + wire);
    }
}
