package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData

interface ActionMergeHandler {
    fun accepts(type: String): Boolean
    fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData)
}