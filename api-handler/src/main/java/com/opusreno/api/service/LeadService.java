package com.opusreno.api.service;

import com.opusreno.api.dto.LeadRequestDto;
import com.opusreno.common.dao.LeadDao;
import com.opusreno.common.domain.Lead;
import com.opusreno.common.queue.SqsPublisher;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.Instant;
import java.util.function.Supplier;

@Slf4j
public class LeadService {

    private final LeadDao leadDao;
    private final SqsPublisher publisher;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Inject
    public LeadService(LeadDao leadDao, SqsPublisher publisher, Clock clock,
                       @Named("idSupplier") Supplier<String> idSupplier) {
        this.leadDao = leadDao;
        this.publisher = publisher;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public String acceptLead(LeadRequestDto dto) {
        String leadId = idSupplier.get();
        Instant now = Instant.now(clock);

        Lead lead = new Lead();
        lead.setLeadId(leadId);
        lead.setGsi1Pk("LEADS");
        lead.setPhone(dto.phone);
        lead.setSource(dto.source.wire());
        lead.setType(dto.type.wire());
        lead.setScope(dto.scope.wire());
        lead.setBudget(dto.budget != null ? dto.budget.wire() : null);
        lead.setBudgetRank(dto.budget != null ? dto.budget.rank() : null);
        lead.setTimeline(dto.timeline != null ? dto.timeline.wire() : null);
        lead.setName(dto.name);
        lead.setEmail(dto.email);
        lead.setArea(dto.area);
        lead.setMessage(dto.message);
        lead.setSessionId(dto.sessionId);
        lead.setCreatedAt(now);

        leadDao.save(lead);
        log.info("lead saved leadId={}", leadId);

        try {
            publisher.publish(leadId);
        } catch (Exception e) {
            log.error("SQS publish failed for leadId={}, lead is still saved", leadId, e);
        }

        return leadId;
    }
}
