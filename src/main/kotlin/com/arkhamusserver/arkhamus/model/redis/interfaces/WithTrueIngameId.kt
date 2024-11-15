package com.arkhamusserver.arkhamus.model.redis.interfaces

interface WithTrueIngameId : WithStringId {
    fun inGameId(): Long
    override fun stringId (): String = inGameId().toString()
}