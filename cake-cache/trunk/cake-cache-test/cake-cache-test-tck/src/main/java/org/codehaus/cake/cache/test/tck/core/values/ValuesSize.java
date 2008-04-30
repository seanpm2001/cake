/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.cache.test.tck.core.values;

import java.util.concurrent.TimeUnit;

import org.codehaus.cake.cache.test.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ValuesSize.java 415 2007-11-09 08:25:23Z kasper $
 */
public class ValuesSize extends AbstractCacheTCKTest {

    /**
     * size returns the correct values.
     */
    @Test
    public void sizeValues() {
        assertEquals(0, newCache().keySet().size());
        assertEquals(5, newCache(5).values().size());
    }

    /**
     * {@link Cache#size()} lazy starts the cache.
     */
    @Test
    public void sizeLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.values().size();
        checkLazystart();
    }

    /**
     * {@link Cache#isEmpty()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void sizeShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        c.values().size();

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        int size = c.values().size();
        assertEquals(0, size);// cache should be empty
    }

}
