package com.ufo.lottery.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lichengan
 */
public final class SoftHashMap<K, V> extends AbstractMap<K, V> {
    private Map<K, SoftValue<V>> hash = new HashMap<K, SoftValue<V>>();
    private final ReferenceQueue<SoftValue<V>> queue = new ReferenceQueue<SoftValue<V>>();

    public SoftHashMap() {
    }

    @Override
    public V get(Object key) {
        V res = null;
        SoftValue<V> sr = hash.get(key);
        if (sr != null) {
            res = sr.get();
            if (res == null)
                hash.remove(key);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private void processQueue() {
        for (; ; ) {
            SoftValue<V> sv = (SoftValue<V>) queue.poll();
            if (sv != null) {
                hash.remove(sv.key);
            } else {
                return;
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object put(Object key, Object value) {
        processQueue();
        return hash.put((K) key, new SoftValue(value, key, queue));
    }

    @Override
    public V remove(Object key) {
        processQueue();
        return hash.remove(key).get();
    }

    @Override
    public void clear() {
        processQueue();
        hash.clear();
    }

    @Override
    public int size() {
        processQueue();
        return hash.size();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set entrySet() {
        throw new UnsupportedOperationException();
    }

    private static class SoftValue<V> extends SoftReference<V> {
        private final Object key;

        private SoftValue(V value, Object key, ReferenceQueue<V> q) {
            super(value, q);
            this.key = key;
        }
    }
}
