/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://cake.codehaus.org/LICENSE
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.internal.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.cake.attribute.AttributeMap;
import org.codehaus.cake.attribute.Attributes;
import org.codehaus.cake.cache.Cache;
import org.codehaus.cake.cache.CacheConfiguration;
import org.codehaus.cake.cache.CacheEntry;
import org.codehaus.cake.cache.service.loading.CacheLoadingService;
import org.codehaus.cake.cache.service.memorystore.MemoryStoreService;
import org.codehaus.cake.forkjoin.collections.ParallelArray;
import org.codehaus.cake.internal.cache.service.attribute.MemorySparseAttributeService;
import org.codehaus.cake.internal.cache.service.loading.DefaultCacheLoadingService;
import org.codehaus.cake.internal.cache.service.loading.InternalCacheLoader;
import org.codehaus.cake.internal.cache.service.loading.UnsynchronizedCacheLoader;
import org.codehaus.cake.internal.cache.service.memorystore.AddSingleEntry;
import org.codehaus.cake.internal.cache.service.memorystore.HashMapMemoryStore;
import org.codehaus.cake.internal.cache.service.memorystore.SingleEntryUpdate;
import org.codehaus.cake.internal.cache.service.memorystore.views.UnsynchronizedCollectionViews;
import org.codehaus.cake.internal.service.Composer;
import org.codehaus.cake.internal.service.UnsynchronizedRunState;
import org.codehaus.cake.internal.util.CollectionUtils;
import org.codehaus.cake.service.Container;
import org.codehaus.cake.service.ServiceManager;

@Container.SupportedServices( { MemoryStoreService.class, CacheLoadingService.class, ServiceManager.class })
public class UnsynchronizedInternalCache<K, V> extends AbstractInternalCache<K, V> {

    InternalCacheLoader<K, V> loader;

    /**
     * Creates a new UnsynchronizedInternalCache with default configuration.
     */
    public UnsynchronizedInternalCache() {
        this(CacheConfiguration.<K, V> newConfiguration());
    }

    public UnsynchronizedInternalCache(CacheConfiguration<K, V> configuration) {
        this(createComposer(configuration));
    }

    public UnsynchronizedInternalCache(CacheConfiguration<K, V> configuration, Cache<K, V> wrapper) {
        this(createComposer(configuration, wrapper));
    }

    private UnsynchronizedInternalCache(Composer composer) {
        super(composer);
        loader = composer.getIfAvailable(InternalCacheLoader.class);
    }

    public void clear() {
        lazyStart();
        long started = listener.beforeCacheClear();
        ParallelArray<CacheEntry<K, V>> list = memoryCache.removeAll();

        listener.afterCacheClear(started, list.asList());
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        for (CacheEntry<K, V> entry : this) {
            if (entry.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public Map<K, V> getAll(Iterable<? extends K> keys) {
        HashMap<K, V> result = new HashMap<K, V>();
        for (K key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    public CacheEntry<K, V> getEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        lazyStartFailIfShutdown();
        CacheEntry<K, V> entry = memoryCache.get(key);
        if (entry == null && loader != null) {
            entry = loader.load(key, Attributes.EMPTY_ATTRIBUTE_MAP);
        }
        return entry;
    }

    public Iterator<CacheEntry<K, V>> iterator() {
        lazyStart();
        return memoryCache.iterator();
    }

    public CacheEntry<K, V> peekEntry(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        lazyStart();
        return memoryCache.peek(key);
    }

    void process(AddSingleEntry<K, V> entry) {
        lazyStartFailIfShutdown();
        listener.beforePut(entry);
        memoryCache.add(entry);
        listener.afterPut(entry);
    }

    public void putAllWithAttributes(Map<K, Map.Entry<V, AttributeMap>> data) {
        long started = listener.beforePutAll(null, null, false);

        lazyStartFailIfShutdown();
        for (Map.Entry<K, Map.Entry<V, AttributeMap>> entry : data.entrySet()) {
            if (entry.getKey() == null) {
                throw new NullPointerException();
            }
            if (entry.getValue().getKey() == null) {
                throw new NullPointerException();
            }
        }
        Map<CacheEntry<K, V>, CacheEntry<K, V>> result = memoryCache.putAllWithAttributes(data);
        ParallelArray<CacheEntry<K, V>> trimmed = memoryCache.trim();

        listener.afterPutAll(started, trimmed, (Map) result, false);
    }

    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        CacheEntry<K, V> removed = removeByKey(key, null);
        return removed == null ? null : removed.getValue();
    }

    public boolean remove(Object key, Object value) {
        checkKeyValue(key, value);
        return removeByKey(key, value) != null;
    }

    public void removeAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("collection is null");
        }
        CollectionUtils.checkCollectionForNulls(keys);
        long started = listener.beforeRemoveAll((Collection) keys);

        lazyStart();
        ParallelArray<CacheEntry<K, V>> list = memoryCache.removeAll(keys);

        listener.afterRemoveAll(started, (Collection) keys, list.asList());

        // return list.size() > 0;
    }

    /** {@inheritDoc} */
    private CacheEntry<K, V> removeByKey(Object key, Object value) {
        long started = listener.beforeRemove(key, value);

        lazyStart();
        final CacheEntry<K, V> e;
        if (value == null) {
            e = memoryCache.remove(key);
        } else {
            e = memoryCache.remove(key, value);
        }

        listener.afterRemove(started, e);
        return e;
    }

    public V replace(K key, V value) {
        checkKeyValue(key, value);
        CacheEntry<K, V> prev = replace(key, null, value, Attributes.EMPTY_ATTRIBUTE_MAP).getPrevious();
        return prev == null ? null : prev.getValue();
    }

    public boolean replace(K key, V oldValue, V newValue) {
        checkReplace(key, oldValue, newValue);
        CacheEntry<K, V> newEntry = replace(key, oldValue, newValue, Attributes.EMPTY_ATTRIBUTE_MAP).getNewEntry();
        return newEntry != null;
    }

    private SingleEntryUpdate<K, V> replace(K key, V oldValue, V newValue, AttributeMap attributes) {
        lazyStartFailIfShutdown();
        SingleEntryUpdate pair = memoryCache.replace(key, oldValue, newValue, attributes);
        return pair;
    }

    public int size() {
        lazyStart();
        return memoryCache.getSize();
    }


    private static Composer createComposer(CacheConfiguration<?, ?> configuration, Cache<?, ?> cache) {
        Composer composer = createComposer(configuration);
        composer.registerInstance(Cache.class, cache);
        composer.registerInstance(Container.class, cache);
        return composer;
    }

    private static Composer createComposer(CacheConfiguration<?, ?> configuration) {
        Composer composer = newComposer(configuration);

        // Common components
        composer.registerImplementation(UnsynchronizedRunState.class);
        if (configuration.withManagement().isEnabled()) {
            throw new IllegalArgumentException("Cache does not support Management");
        }

        // Cache components
        composer.registerImplementation(UnsynchronizedCollectionViews.class);
        composer.registerImplementation(HashMapMemoryStore.class);

        // composer.registerImplementation(DefaultAttributeService.class);
        composer.registerImplementation(MemorySparseAttributeService.class);

        if (configuration.withLoading().getLoader() != null) {
            composer.registerImplementation(UnsynchronizedCacheLoader.class);
            composer.registerImplementation(DefaultCacheLoadingService.class);
        }
        return composer;
    }

}
