package com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces

import java.util.*

open class RamCrudRepository<T: RedisGameEntity>: MyCrudRepository<T> {

    protected val map: MutableMap<String, T> = HashMap()

    override fun findByGameId(gameId: Long): List<T> = map.values.filter { it.gameId == gameId }

    override fun findById(id: String): Optional<T> = Optional.ofNullable(map[id])

    override fun existsById(id: String): Boolean = map[id] != null

    override fun findAll(): Iterable<T> = map.values

    override fun findAllById(ids: Iterable<String>): Iterable<T> = ids.mapNotNull { map[it] }

    override fun count(): Long = map.count().toLong()

    override fun deleteById(id: String) {
        map.remove(id)
    }

    override fun deleteAllById(ids: Iterable<String>) {
        ids.forEach{deleteById(it)}
    }

    override fun deleteAll() {
        map.clear()
    }

    override fun deleteAll(entities: Iterable<T>) {
        entities.forEach{ map.remove(it.id) }
    }

    override fun delete(entity: T) {
        deleteById(entity.id)
    }

    override fun <S : T> saveAll(entities: Iterable<S>): Iterable<S> {
        entities.forEach{ map[it.id] = it }
        return entities
    }

    override fun <S : T> save(entity: S): S {
        map[entity.id] = entity
        return entity
    }
}