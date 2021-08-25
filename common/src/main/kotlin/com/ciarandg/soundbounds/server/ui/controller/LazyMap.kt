package com.ciarandg.soundbounds.server.ui.controller

open class LazyMap<K, V>(private val constructor: (key: K) -> V) : Map<K, V> {
    private val innerMap: MutableMap<K, V> = HashMap()

    override val entries: Set<Map.Entry<K, V>>
        get() = innerMap.entries
    override val keys: Set<K>
        get() = innerMap.keys
    override val size: Int
        get() = innerMap.size
    override val values: Collection<V>
        get() = innerMap.values

    override fun containsKey(key: K) = innerMap.containsKey(key)

    override fun containsValue(value: V) = innerMap.containsValue(value)

    override fun get(key: K): V = innerMap.getOrPut(key) { constructor(key) }

    override fun isEmpty() = innerMap.isEmpty()
}
