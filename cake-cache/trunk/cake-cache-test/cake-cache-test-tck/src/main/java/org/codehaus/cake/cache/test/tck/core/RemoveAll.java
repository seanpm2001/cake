/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.cache.test.tck.core;

import org.codehaus.cake.cache.test.tck.AbstractCacheTCKTest;
import org.codehaus.cake.test.util.CollectionTestUtil;
import org.junit.Test;

public class RemoveAll extends AbstractCacheTCKTest {
    // TODO: remove, removeAll

    @Test(expected = NullPointerException.class)
    public void removeAllNPE1() {
        init();
        c.removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeAllNPE() {
        init();
        c.removeAll(CollectionTestUtil.keysWithNull);

    }

    @Test
    public void removeAll() {
        init();
        c.removeAll(CollectionTestUtil.asList(2, 3));

        c = newCache(5);
        c.removeAll(CollectionTestUtil.asList(2, 3));
        assertSize(3);

        c = newCache(5);
        c.removeAll(CollectionTestUtil.asList(5, 6));
        assertSize(4);
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void removeAllLazyStart() {
        init();
        assertFalse(c.isStarted());
        c.removeAll(CollectionTestUtil.asList(2, 3));
        checkLazystart();
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     */
    @Test
    public void removeAllShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.removeAll(CollectionTestUtil.asList(2, 3));
    }
}
