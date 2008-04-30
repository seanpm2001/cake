/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.cache.test.tck.core.entryset;

import java.util.Collections;

import org.codehaus.cake.cache.test.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests the add method for {@link Cache#entrySet()}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: EntrySetAdd.java 511 2007-12-13 14:37:02Z kasper $
 */
public class EntrySetAdd extends AbstractCacheTCKTest {

    /**
     * Tests that {@link Set#addAll(java.util.Collection)} throws a
     * {@link NullPointerException} when invoked with <code>null</code>.
     */
    @Test(expected = NullPointerException.class)
    public void addAllNPE() {
        try {
            newCache().entrySet().addAll(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // ok
        }
    }

    /**
     * Tests that {@link Set#addAll(java.util.Collection)} throws a
     * {@link UnsupportedOperationException} when invoked with a valid element.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void addAllUOE() {
        newCache().entrySet().addAll(Collections.singleton(M1));
    }

    /**
     * Tests that {@link Set#add(Object)} throws a {@link NullPointerException} when
     * invoked with <code>null</code>.
     */
    @Test(expected = NullPointerException.class)
    public void addNPE() {
        try {
            newCache().entrySet().add(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    /**
     * Tests that {@link Set#add(Object)} throws a {@link UnsupportedOperationException}
     * when invoked with a valid element.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void addUOE() {
        newCache().entrySet().add(M1);
    }
}
