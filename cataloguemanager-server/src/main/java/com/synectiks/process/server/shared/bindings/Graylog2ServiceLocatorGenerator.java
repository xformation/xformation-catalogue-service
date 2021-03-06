/*
 * */
package com.synectiks.process.server.shared.bindings;

import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.external.generator.ServiceLocatorGeneratorImpl;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.jvnet.hk2.guice.bridge.api.HK2IntoGuiceBridge;

public class Graylog2ServiceLocatorGenerator extends ServiceLocatorGeneratorImpl {
    @Override
    public ServiceLocator create(String name, ServiceLocator parent) {
        final ServiceLocator serviceLocator = super.create(name, parent);

        final Injector injector = GuiceInjectorHolder.getInjector()
                .createChildInjector(new HK2IntoGuiceBridge(serviceLocator));

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        final GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector);

        return serviceLocator;
    }
}
