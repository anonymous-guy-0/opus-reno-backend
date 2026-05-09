package com.opusreno.api.di;

import com.opusreno.api.router.RequestRouter;
import dagger.Component;
import jakarta.inject.Singleton;

@Singleton
@Component(modules = {ApiModule.class})
public interface ApiComponent {

    RequestRouter router();

}
