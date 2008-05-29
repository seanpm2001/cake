package org.codehaus.cake.internal.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.codehaus.cake.internal.UseInternals;
import org.codehaus.cake.internal.picocontainer.MutablePicoContainer;
import org.codehaus.cake.internal.picocontainer.defaults.DefaultPicoContainer;
import org.codehaus.cake.internal.service.spi.CompositeService;
import org.codehaus.cake.internal.service.spi.ContainerInfo;
import org.codehaus.cake.service.ContainerConfiguration;
import org.codehaus.cake.service.ServiceManager;
import org.codehaus.cake.service.ServiceRegistrant;

@UseInternals
public class Composer {

    /** The picocontainer used to wire servicers. */
    private final MutablePicoContainer baseContainer;

    private final MutablePicoContainer container;

    // private List ex
    public Composer(Class clazz, ContainerConfiguration configuration) {
        baseContainer = new DefaultPicoContainer();
        baseContainer.registerComponentInstance(configuration);
        baseContainer.registerComponentInstance(configuration.getClock());
        for (Object c : configuration.getConfigurations()) {
            baseContainer.registerComponentInstance(c);
        }
        container = baseContainer.makeChildContainer();
        container.registerComponentInstance(this);
        container.registerComponentInstance(new ContainerInfo(clazz, configuration));
        container.registerComponentImplementation(LifecycleManager.class);
        container.registerComponentImplementation(ServiceManager.class, DefaultServiceManager.class);
        container.registerComponentImplementation(ServiceRegistrant.class, DefaultServiceRegistrant.class);
    }

    public void registerImplementation(Class<?> clazz) {
        container.registerComponentImplementation(clazz);
    }

    public void registerImplementation(Object key, Class<?> clazz) {
        container.registerComponentImplementation(key, clazz);
    }

    public void registerInstance(Object value) {
        container.registerComponentInstance(value, value);
    }

    public void registerInstance(Object key, Object value) {
        container.registerComponentInstance(key, value);
    }

    public <T> boolean hasService(Class<T> serviceType) {
        return container.getComponentAdapterOfType(serviceType) != null;
    }

    public <T> T get(Class<T> serviceType) {
        T service = (T) container.getComponentInstanceOfType(serviceType);
        if (service == null) {
            throw new IllegalArgumentException("Unknown service: " + serviceType);
        }
        return service;
    }

    public <T> T getIfAvailable(Class<T> serviceType) {
        T service = (T) container.getComponentInstanceOfType(serviceType);
        return service;
    }

    public Object getFromKeyIfAvailable(Object key) {
        return container.getComponentInstance(key);
    }

    public Set<?> prepareStart() {
        return prepareStart(get(ContainerConfiguration.class));
    }

    public Set<?> prepareStart(ContainerConfiguration conf) {
        Set result = new LinkedHashSet(container.getComponentInstances());
        for (Object object : new ArrayList(result)) {
            if (object instanceof CompositeService) {
                for (Object oo : ((CompositeService) object).getChildServices()) {
                    result.add(oo);
                }
            }
        }
        result.addAll(conf.getServices());
        return result;
    }
}
