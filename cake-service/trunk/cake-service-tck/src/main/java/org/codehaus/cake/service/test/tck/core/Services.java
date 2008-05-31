package org.codehaus.cake.service.test.tck.core;

import java.util.Set;

import org.codehaus.cake.attribute.Attributes;
import org.codehaus.cake.service.Container;
import org.codehaus.cake.service.ContainerConfiguration;
import org.codehaus.cake.service.ServiceRegistrant;
import org.codehaus.cake.service.Startable;
import org.codehaus.cake.service.test.tck.AbstractTCKTest;
import org.junit.Test;

public class Services extends AbstractTCKTest<Container, ContainerConfiguration> {

    @Test(expected = NullPointerException.class)
    public void getServiceNPE() {
        c.getService(null);
    }

    @Test(expected = NullPointerException.class)
    public void getServiceNPE1() {
        c.getService(null, Attributes.EMPTY_ATTRIBUTE_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void getServiceNPE2() {
        c.getService(Integer.class, null);
    }

    @Test
    public void serviceGet() {
        conf.addService(new Register());
        newContainer();
        assertFalse(c.isStarted());
        assertNotNull(c.getService(Integer.class));
        checkLazystart();
    }

    /**
     * {@link Set#clear()} fails when the cache is shutdown.
     */
    @Test
    public void serviceGetShutdown() {
        conf.addService(new Register());
        newContainer();
        Integer i = c.getService(Integer.class);
        shutdownAndAwaitTermination();
        assertSame(i, c.getService(Integer.class));
    }

    @Test
    public void serviceNoHas() {
        conf.addService(new Register());
        newContainer();
        assertFalse(c.isStarted());
        assertFalse(c.hasService(Double.class));
        checkLazystart();
    }

    @Test
    public void serviceHas() {
        conf.addService(new Register());
        newContainer();
        assertFalse(c.isStarted());
        assertTrue(c.hasService(Integer.class));
        checkLazystart();
    }

    @Test
    public void serviceHasShutdown() {
        conf.addService(new Register());
        newContainer();
        assertTrue(c.hasService(Integer.class));
        shutdownAndAwaitTermination();
        assertTrue(c.hasService(Integer.class));
    }

    public static class Register {
        @Startable
        public void start(ServiceRegistrant registrant) {
            registrant.registerService(Integer.class, new Integer(1000));
        }
    }
}
