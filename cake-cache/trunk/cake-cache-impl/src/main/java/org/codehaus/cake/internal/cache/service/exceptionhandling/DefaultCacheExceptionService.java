package org.codehaus.cake.internal.cache.service.exceptionhandling;

import org.codehaus.cake.attribute.AttributeMap;
import org.codehaus.cake.cache.Cache;
import org.codehaus.cake.cache.service.exceptionhandling.CacheExceptionHandler;
import org.codehaus.cake.internal.service.exceptionhandling.AbstractExceptionService;
import org.codehaus.cake.internal.service.spi.ContainerInfo;
import org.codehaus.cake.service.Container;
import org.codehaus.cake.service.ContainerConfiguration;
import org.codehaus.cake.service.exceptionhandling.ExceptionContext;
import org.codehaus.cake.service.exceptionhandling.ExceptionHandlingConfiguration;
import org.codehaus.cake.util.Logger;

public class DefaultCacheExceptionService<K, V> extends AbstractExceptionService<Cache<K, V>> implements
        InternalCacheExceptionService<K, V> {
    /** The CacheExceptionHandler configured for this cache. */
    private CacheExceptionHandler<K, V> exceptionHandler;

    public DefaultCacheExceptionService(Container container, ContainerInfo info,
            ContainerConfiguration<Cache<K, V>> containerConfiguration,
            ExceptionHandlingConfiguration<CacheExceptionHandler<K, V>> configuration) {
        super(container, info, containerConfiguration, configuration.getExceptionLogger());
        CacheExceptionHandler<K, V> exceptionHandler = configuration.getExceptionHandler();
        this.exceptionHandler = exceptionHandler == null ? new CacheExceptionHandler<K, V>() : exceptionHandler;
    }

    @Override
    protected void handle(ExceptionContext<Cache<K, V>> context) {
        exceptionHandler.handle(context);
    }

    public V loadFailed(Throwable cause, K key, AttributeMap map) {
        String message = "Loading of Value failed [key = " + key + "]";
        return exceptionHandler.loadingOfValueFailed(createContext(cause, message, Logger.Level.Error), key, map);
    }
}
