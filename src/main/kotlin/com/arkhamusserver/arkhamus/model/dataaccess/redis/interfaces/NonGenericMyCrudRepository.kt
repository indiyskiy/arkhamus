package com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces

interface NonGenericMyCrudRepository {
    fun findByGameId(gameId: Long): Iterable<*>
}