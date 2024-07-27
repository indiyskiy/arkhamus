package com.arkhamusserver.arkhamus.model.dataaccess

interface ToDeleteOnGameEnd<T> {
    fun deleteAll(entities: MutableList<Any?>)
    fun findByGameId(gameId: Long): List<T>
}