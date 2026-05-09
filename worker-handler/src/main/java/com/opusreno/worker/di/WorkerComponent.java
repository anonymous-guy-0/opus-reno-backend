package com.opusreno.worker.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opusreno.worker.service.WorkerService;
import dagger.Component;
import jakarta.inject.Singleton;

@Singleton
@Component(modules = {WorkerModule.class})
public interface WorkerComponent {
    WorkerService workerService();
    ObjectMapper objectMapper();
}
