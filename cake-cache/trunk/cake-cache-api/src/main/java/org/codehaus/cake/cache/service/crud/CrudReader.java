package org.codehaus.cake.cache.service.crud;

import java.util.Map;

import org.codehaus.cake.attribute.AttributeMap;
import org.codehaus.cake.attribute.BooleanAttribute;
import org.codehaus.cake.attribute.ObjectAttribute;
import org.codehaus.cake.cache.CacheDataExtractor;
import org.codehaus.cake.cache.CacheEntry;
import org.codehaus.cake.ops.Ops.Op;
import org.codehaus.cake.service.ContainerAlreadyShutdownException;

/**
 * A CrudReader is used for reading data from the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java 520 2007-12-21 17:53:31Z kasper $
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <R>
 *            the type data returned by the reader
 */
public interface CrudReader<K, R> {

    /**
     * An attribute used to indicate that a CrudReader should only look in the in-memory store. It will not try to fetch
     * missing items, it will only return a value if it actually exists in the cache. Furthermore, it will not effect
     * any statistics gathered by the cache.
     */
    BooleanAttribute PEEK = new BooleanAttribute() {};

    BooleanAttribute READ_THROUGH = new BooleanAttribute() {};

    /**
     * This attribute can be used for asserting that a mapping exists for the specified key(s) when calling any of the
     * <tt>get</tt> methods in this interface. This can be useful as a fail-fast approach if a mapping for any valid
     * key should always be present.
     * 
     * 
     * If this attribute is set to <code>true</code> then calling, for example, {@link #get(Object)} with a key for
     * which no mapping exists the method will throw an {@link IllegalStateException}.
     * 
     * The following example shows how this attribute can be set.
     * 
     * <pre>
     * Cache&lt;Integer, String&gt; c = somecache;
     * AttributeMap attributes = CrudReader.ASSERT_GET.singleton(true);
     * c.crud().reader(attributes).get(4); //throws an IllegalStateException (under the condition that no mapping exists)
     * </pre>
     */
    BooleanAttribute ASSERT_GET = new BooleanAttribute() {};

    public enum ReadType {
        PEEK, NORMAL, READ_THROUGH, READ_THROUGH_NO_CACHE
    }

    /**
     * This attributes is used for specifying what type of data the {@link CrudReader} should return.
     * 
     * The default transformer will return the value for any given {@link CacheEntry}
     * <p>
     * When the cache.
     * 
     * If no entry is found for the specified key(s) <code>null</code> the transformer would be getting a
     * <code>null</code> Any transformer should be prepared to accept null values
     * 
     * <p>
     * A transformer should be threadsafe
     */
    ObjectAttribute<Op<?, ?>> READ_TRANSFORMER = (ObjectAttribute) new ObjectAttribute<Op>(Op.class,
            CacheDataExtractor.ONLY_VALUE) {};

    /**
     * Works as {@link Map#get(Object)} with the following modifications.
     * <p>
     * If the cache has a configured CacheLoader. And no mapping exists for the specified key or the specific mapping
     * has expired. The cache will transparently attempt to load a value for the specified key through the cache loader.
     * <p>
     * 
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this cache maps the specified key, or <tt>null</tt> if the cache contains no mapping
     *         for this key.
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this cache (optional).
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     * @throws ContainerAlreadyShutdownException
     *             if the cache has been shutdown
     * @return the associated value
     */
    R get(K key);

    /**
     * As
     * 
     * @param key
     * @param attributes
     * @return
     */
    R get(K key, AttributeMap attributes);

    /**
     * Attempts to retrieve all of the mappings for the specified collection of keys. The effect of this call is
     * equivalent to that of calling {@link #get(Object)} on this cache once for each key in the specified collection.
     * However, in some cases it can be much faster to load several cache items at once, for example, if the cache must
     * fetch the values from a remote host. This is equivalent to
     * 
     * <pre>
     * Map&lt;K, R&gt; result = new HashMap&lt;K, R&gt;();
     * for (K key : keys) {
     *     result.put(key, get(key));
     * }
     * return result;
     * </pre>
     * 
     * <p>
     * Note that if {@link #get(Object)} returns <code>null</code> the map returned will contain a mapping from the
     * given key to <code>null</code>.
     * <p>
     * The behavior of this operation is unspecified if the specified collection is modified while the operation is in
     * progress.
     * 
     * @param keys
     *            a collection of keys whose associated values are to be returned.
     * @return a map with mappings from each key to the corresponding value (which can be null)
     * @throws ClassCastException
     *             if any of the keys in the specified collection are of an inappropriate type for this cache
     *             (optional).
     * @throws NullPointerException
     *             if the specified collection of keys is null or the specified collection contains a null
     * @throws IllegalStateException
     *             if the cache has been shutdown
     */
    Map<K, R> getAll(Iterable<? extends K> keys);
}
