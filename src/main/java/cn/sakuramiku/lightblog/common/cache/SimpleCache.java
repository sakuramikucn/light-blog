package cn.sakuramiku.lightblog.common.cache;

import cn.hutool.core.lang.func.Func0;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleCache<K, V> implements Iterable<Map.Entry<K, V>>, Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<K, V> cache;
    private final ReentrantReadWriteLock lock;

    public SimpleCache() {
        this(new WeakHashMap());
    }

    public SimpleCache(Map<K, V> initMap) {
        this.lock = new ReentrantReadWriteLock();
        this.cache = initMap;
    }

    public V get(K key) {
        this.lock.readLock().lock();

        Object var2;
        try {
            var2 = this.cache.get(key);
        } finally {
            this.lock.readLock().unlock();
        }

        return (V) var2;
    }

    public V get(K key, Func0<V> supplier) {
        V v = this.get(key);
        if (null == v && null != supplier) {
            this.lock.writeLock().lock();

            try {
                v = this.cache.get(key);
                if (null == v) {
                    try {
                        v = supplier.call();
                    } catch (Exception var8) {
                        throw new RuntimeException(var8);
                    }

                    this.cache.put(key, v);
                }
            } finally {
                this.lock.writeLock().unlock();
            }
        }

        return v;
    }

    public V put(K key, V value) {
        this.lock.writeLock().lock();

        try {
            this.cache.put(key, value);
        } finally {
            this.lock.writeLock().unlock();
        }

        return value;
    }

    public V remove(K key) {
        this.lock.writeLock().lock();

        Object var2;
        try {
            var2 = this.cache.remove(key);
        } finally {
            this.lock.writeLock().unlock();
        }

        return (V) var2;
    }

    public void clear() {
        this.lock.writeLock().lock();

        try {
            this.cache.clear();
        } finally {
            this.lock.writeLock().unlock();
        }

    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return this.cache.entrySet().iterator();
    }
}