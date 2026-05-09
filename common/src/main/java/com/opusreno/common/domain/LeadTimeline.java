package com.opusreno.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeadTimeline {
    ONE_TO_THREE_MONTHS("1-3_months"),
    THREE_TO_SIX_MONTHS("3-6_months"),
    SIX_PLUS_MONTHS("6+_months");

    private final String wire;

    LeadTimeline(String wire) { this.wire = wire; }

    @JsonValue
    public String wire() { return wire; }

    @JsonCreator
    public static LeadTimeline fromWire(String wire) {
        for (LeadTimeline t : values()) {
            if (t.wire.equals(wire)) return t;
        }
        throw new IllegalArgumentException("unknown LeadTimeline wire value: " + wire);
    }
}
