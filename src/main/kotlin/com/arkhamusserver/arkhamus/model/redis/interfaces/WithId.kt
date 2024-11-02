package com.arkhamusserver.arkhamus.model.redis.interfaces

interface WithId : WithStringId {
    fun inGameId(): Long
    override fun stringId (): String = inGameId().toString()
}