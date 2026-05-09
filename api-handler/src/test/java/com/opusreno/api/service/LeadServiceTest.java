package com.opusreno.api.service;

import com.opusreno.api.dto.LeadRequestDto;
import com.opusreno.common.dao.LeadDao;
import com.opusreno.common.domain.Lead;
import com.opusreno.common.domain.LeadBudget;
import com.opusreno.common.domain.LeadScope;
import com.opusreno.common.domain.LeadSource;
import com.opusreno.common.domain.LeadTimeline;
import com.opusreno.common.domain.LeadType;
import com.opusreno.common.queue.SqsPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock private LeadDao leadDao;
    @Mock private SqsPublisher publisher;

    private LeadService service;
    private static final Instant FIXED_TIME = Instant.parse("2026-05-08T12:00:00Z");
    private static final String FIXED_ID = "test-lead-id";

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
        service = new LeadService(leadDao, publisher, clock, () -> FIXED_ID);
    }

    @Test
    void acceptLead_happyPath_savesAndPublishes() {
        LeadRequestDto dto = validDto();
        when(publisher.publish(FIXED_ID)).thenReturn("msg-123");

        String leadId = service.acceptLead(dto);

        assertThat(leadId).isEqualTo(FIXED_ID);
        ArgumentCaptor<Lead> captor = ArgumentCaptor.forClass(Lead.class);
        verify(leadDao).save(captor.capture());

        Lead saved = captor.getValue();
        assertThat(saved.getLeadId()).isEqualTo(FIXED_ID);
        assertThat(saved.getGsi1Pk()).isEqualTo("LEADS");
        assertThat(saved.getPhone()).isEqualTo("+919876543210");
        assertThat(saved.getSource()).isEqualTo("heroCta");
        assertThat(saved.getType()).isEqualTo("homeowner");
        assertThat(saved.getScope()).isEqualTo("fullRenovation");
        assertThat(saved.getBudget()).isEqualTo("5L-10L");
        assertThat(saved.getBudgetRank()).isEqualTo(3);
        assertThat(saved.getTimeline()).isEqualTo("1-3_months");
        assertThat(saved.getName()).isEqualTo("Rahul Sharma");
        assertThat(saved.getCreatedAt()).isEqualTo(FIXED_TIME);

        verify(publisher).publish(FIXED_ID);
    }

    @Test
    void acceptLead_nullOptionalFields_savesWithNulls() {
        LeadRequestDto dto = validDto();
        dto.budget = null;
        dto.timeline = null;
        dto.email = null;
        dto.area = null;
        dto.message = null;

        service.acceptLead(dto);

        ArgumentCaptor<Lead> captor = ArgumentCaptor.forClass(Lead.class);
        verify(leadDao).save(captor.capture());
        Lead saved = captor.getValue();
        assertThat(saved.getBudget()).isNull();
        assertThat(saved.getBudgetRank()).isNull();
        assertThat(saved.getTimeline()).isNull();
        assertThat(saved.getEmail()).isNull();
    }

    @Test
    void acceptLead_sqsFailure_stillReturnsLeadId() {
        LeadRequestDto dto = validDto();
        when(publisher.publish(any())).thenThrow(new RuntimeException("SQS down"));

        String leadId = service.acceptLead(dto);

        assertThat(leadId).isEqualTo(FIXED_ID);
        verify(leadDao).save(any(Lead.class));
    }

    private LeadRequestDto validDto() {
        LeadRequestDto dto = new LeadRequestDto();
        dto.name = "Rahul Sharma";
        dto.phone = "+919876543210";
        dto.email = "rahul@example.com";
        dto.source = LeadSource.HERO_CTA;
        dto.type = LeadType.HOMEOWNER;
        dto.scope = LeadScope.FULL_RENOVATION;
        dto.budget = LeadBudget.FIVE_TO_10L;
        dto.timeline = LeadTimeline.ONE_TO_THREE_MONTHS;
        dto.area = "Koregaon Park";
        dto.message = "Need full renovation";
        dto.sessionId = "sess-123";
        dto.website = null;
        dto.formOpenedAt = System.currentTimeMillis() - 5000;
        return dto;
    }
}
