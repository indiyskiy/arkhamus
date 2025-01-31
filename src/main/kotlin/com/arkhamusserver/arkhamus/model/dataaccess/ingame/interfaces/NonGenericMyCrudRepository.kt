package com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces

interface NonGenericMyCrudRepository {
    fun findByGameId(gameId: Long): Iterable<*>
}