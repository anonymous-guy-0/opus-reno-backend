package com.opusreno.worker.service;

import com.opusreno.common.dao.LeadDao;
import com.opusreno.common.domain.Lead;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class WorkerService {

    private final LeadDao leadDao;
    private final Clock clock;

    public void process(String leadId) {
        Optional<Lead> lead = leadDao.findById(leadId);

        if (lead.isEmpty()) {
            log.warn("lead not found, acking silently leadId={}", leadId);
            return;
        }

        leadDao.markProcessed(leadId, Instant.now(clock));
        log.info("lead processed leadId={}", leadId);
    }
}
