package com.opusreno.common.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opusreno.common.json.ObjectMapperFactory;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;

@Module
public class CommonModule {

    @Provides
    @Singleton
    public ObjectMapper objectMapper() {
        return new ObjectMapperFactory().create();
    }
}
