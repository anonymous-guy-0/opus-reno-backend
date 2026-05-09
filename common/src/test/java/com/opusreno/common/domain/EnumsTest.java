package com.opusreno.common.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumsTest {

    @ParameterizedTest
    @EnumSource(LeadSource.class)
    void leadSource_roundTrip(LeadSource source) {
        assertThat(LeadSource.fromWire(source.wire())).isEqualTo(source);
    }

    @ParameterizedTest
    @EnumSource(LeadType.class)
    void leadType_roundTrip(LeadType type) {
        assertThat(LeadType.fromWire(type.wire())).isEqualTo(type);
    }

    @ParameterizedTest
    @EnumSource(LeadScope.class)
    void leadScope_roundTrip(LeadScope scope) {
        assertThat(LeadScope.fromWire(scope.wire())).isEqualTo(scope);
    }

    @ParameterizedTest
    @EnumSource(LeadBudget.class)
    void leadBudget_roundTrip(LeadBudget budget) {
        assertThat(LeadBudget.fromWire(budget.wire())).isEqualTo(budget);
    }

    @ParameterizedTest
    @EnumSource(LeadTimeline.class)
    void leadTimeline_roundTrip(LeadTimeline timeline) {
        assertThat(LeadTimeline.fromWire(timeline.wire())).isEqualTo(timeline);
    }

    @Test
    void leadBudget_rankOrdering() {
        assertThat(LeadBudget.FIFTY_TO_1L.rank()).isEqualTo(1);
        assertThat(LeadBudget.TWENTYFIVE_L_PLUS.rank()).isEqualTo(5);
    }

    @Test
    void leadSource_unknownWire_throws() {
        assertThatThrownBy(() -> LeadSource.fromWire("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leadBudget_unknownWire_throws() {
        assertThatThrownBy(() -> LeadBudget.fromWire("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
