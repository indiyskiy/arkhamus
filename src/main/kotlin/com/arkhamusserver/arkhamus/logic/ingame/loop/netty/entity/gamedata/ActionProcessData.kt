package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata

interface ActionProcessData {
    fun executedSuccessfully(): Boolean
    fun updateExecutedSuccessfully(executedSuccessfully: Boolean)
}
