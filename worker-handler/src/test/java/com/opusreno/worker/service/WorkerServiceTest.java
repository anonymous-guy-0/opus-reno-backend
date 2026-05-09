package com.opusreno.worker.service;

import com.opusreno.common.dao.LeadDao;
import com.opusreno.common.domain.Lead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerServiceTest {

    @Mock private LeadDao leadDao;

    private WorkerService service;
    private static final Instant FIXED_TIME = Instant.parse("2026-05-09T12:00:00Z");

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
        service = new WorkerService(leadDao, clock);
    }

    @Test
    void process_leadExists_marksProcessed() {
        Lead lead = new Lead();
        lead.setLeadId("lead-123");
        when(leadDao.findById("lead-123")).thenReturn(Optional.of(lead));

        service.process("lead-123");

        verify(leadDao).markProcessed("lead-123", FIXED_TIME);
    }

    @Test
    void process_leadNotFound_acksWithoutMarkingProcessed() {
        when(leadDao.findById("missing-id")).thenReturn(Optional.empty());

        service.process("missing-id");

        verify(leadDao, never()).markProcessed(any(), any());
    }

    @Test
    void process_daoThrows_exceptionPropagates() {
        when(leadDao.findById("lead-123")).thenReturn(Optional.of(new Lead()));
        doThrow(new RuntimeException("DDB error"))
                .when(leadDao).markProcessed(any(), any());

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> service.process("lead-123"));
    }
}
