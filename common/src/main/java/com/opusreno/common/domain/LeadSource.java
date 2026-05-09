package com.opusreno.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeadSource {
    HERO_CTA("heroCta"),
    NAV_CTA("navCta"),
    BOTTOM_CTA("bottomCta");

    private final String wire;

    LeadSource(String wire) { this.wire = wire; }

    @JsonValue
    public String wire() { return wire; }

    @JsonCreator
    public static LeadSource fromWire(String wire) {
        for (LeadSource s : values()) {
            if (s.wire.equals(wire)) return s;
        }
        throw new IllegalArgumentException("unknown LeadSource wire value: " + wire);
    }
}
