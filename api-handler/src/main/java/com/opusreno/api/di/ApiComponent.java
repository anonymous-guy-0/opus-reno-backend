package com.opusreno.api.di;

import com.opusreno.api.router.RequestRouter;
import com.opusreno.common.di.CommonModule;
import dagger.Component;
import jakarta.inject.Singleton;

@Singleton
@Component(modules = {CommonModule.class, ApiModule.class})
public interface ApiComponent {

    RequestRouter router();

}
