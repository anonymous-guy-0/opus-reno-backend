package com.opusreno.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeadBudget {
    FIFTY_TO_1L("50k-1L", 1),
    ONE_TO_5L("1L-5L", 2),
    FIVE_TO_10L("5L-10L", 3),
    TEN_TO_25L("10L-25L", 4),
    TWENTYFIVE_L_PLUS("25L+", 5);

    private final String wire;
    private final int rank;

    LeadBudget(String wire, int rank) {
        this.wire = wire;
        this.rank = rank;
    }

    @JsonValue
    public String wire() { return wire; }

    public int rank() { return rank; }

    @JsonCreator
    public static LeadBudget fromWire(String wire) {
        for (LeadBudget b : values()) {
            if (b.wire.equals(wire)) return b;
        }
        throw new IllegalArgumentException("unknown LeadBudget wire value: " + wire);
    }
}
