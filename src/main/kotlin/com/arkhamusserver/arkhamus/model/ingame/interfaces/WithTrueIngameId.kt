package com.arkhamusserver.arkhamus.model.ingame.interfaces

interface WithTrueIngameId : WithStringId {
    fun inGameId(): Long
    override fun stringId(): String = inGameId().toString()
}