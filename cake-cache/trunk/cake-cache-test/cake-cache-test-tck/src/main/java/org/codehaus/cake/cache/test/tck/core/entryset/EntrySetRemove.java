/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.cache.test.tck.core.entryset;

import static org.codehaus.cake.test.util.CollectionTestUtil.MNAN1;
import static org.codehaus.cake.test.util.CollectionTestUtil.MNAN2;

import java.util.Arrays;

import org.codehaus.cake.cache.test.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests the modifying functions of a keySet().
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: EntrySetRemove.java 554 2008-01-08 23:32:04Z kasper $
 */
public class EntrySetRemove extends AbstractCacheTCKTest {

    @Test(expected = NullPointerException.class)
    public void removeNPE() {
        newCache(0).entrySet().remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeNPE2() {
        newCache(5).entrySet().remove(null);
    }

    @Test
    public void remove() {
        init();
        assertFalse(c.entrySet().remove(1));
        assertFalse(c.entrySet().remove(MNAN1));
        c = newCache(5);
        assertTrue(c.entrySet().remove(M1));
        assertSize(4);
        assertFalse(c.entrySet().contains(M1));

        c = newCache(1);
        assertTrue(c.entrySet().remove(M1));
        assertTrue(c.isEmpty());
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void removeLazyStart() {
        init();
        assertFalse(c.isStarted());
        c.entrySet().remove(MNAN1);
        checkLazystart();
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     */
    @Test
    public void removeShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.entrySet().remove(MNAN1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removeAll() {
        c = newCache(5);
        assertFalse(c.entrySet().removeAll(Arrays.asList(MNAN1, MNAN2)));
        assertTrue(c.entrySet().removeAll(Arrays.asList(MNAN1, M2, MNAN2)));
        assertSize(4);
        assertFalse(c.entrySet().contains(M2));
        assertTrue(c.entrySet().removeAll(Arrays.asList(M1, M4)));
        assertFalse(c.entrySet().contains(M4));
        assertFalse(c.entrySet().contains(M1));
        assertSize(2);
    }

    @Test(expected = NullPointerException.class)
    public void removeAllNPE() {
        newCache(0).entrySet().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeAllNPE1() {
        newCache(5).entrySet().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeAllNPE2() {
        newCache(5).entrySet().removeAll(Arrays.asList(1, null));
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void removeAllLazyStart() {
        init();
        assertFalse(c.isStarted());
        c.entrySet().removeAll(Arrays.asList(M1, M4));
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
        assertFalse(c.entrySet().removeAll(Arrays.asList(M1, M4)));
    }
}
