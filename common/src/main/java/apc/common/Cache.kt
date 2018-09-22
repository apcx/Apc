package apc.common

import java.lang.ref.SoftReference

@Suppress("unused")
class Cache<K, V>(private val create: (K) -> V) {

    private val map = mutableMapOf<K, SoftReference<V>>()

    operator fun get(key: K): V {
        var value = map[key]?.get()
        if (value == null) {
            value = create(key)
            map[key] = SoftReference(value)
        }
        return value!!
    }
}