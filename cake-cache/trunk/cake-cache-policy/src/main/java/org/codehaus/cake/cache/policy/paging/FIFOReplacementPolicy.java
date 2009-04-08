/*
 * Copyright 2008, 2009 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.codehaus.cake.cache.policy.paging;

import org.codehaus.cake.cache.policy.AbstractDoubleLinkedReplacementPolicy;
import org.codehaus.cake.cache.policy.PolicyContext;

/**
 * A First In, First Out (FIFO) based replacement policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <T>
 *            the type of elements being cached
 */
public class FIFOReplacementPolicy<T> extends AbstractDoubleLinkedReplacementPolicy<T> {

    /** The name of the policy. */
    public static final String NAME = "FIFO";

    /**
     * Creates a new FIFOReplacementPolicy.
     * 
     * @param context
     *            a policy context instance
     * @throws NullPointerException
     *             if the specified context is null
     */
    public FIFOReplacementPolicy(PolicyContext<T> context) {
        super(context);
    }

    /** {@inheritDoc} */
    public void add(T entry) {
        addFirst(entry);
    }

    /** {@inheritDoc} */
    public T evictNext() {
        return removeFirst();
    }
}
