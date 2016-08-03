package com.gamecard.dagger.component;

import com.gamecard.dagger.module.AppModule;
import com.gamecard.dagger.module.NetModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bridgeit on 14/7/16.
 */

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {

}
