package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.CraftProcessRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.crafter.CraftProcessRequestMessage
import org.springframework.stereotype.Component

@Component
class CraftProcessMergeHandler : ActionMergeHandler {

    override fun accepts(type: String): Boolean = type == CraftProcessRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is CraftProcessRequestProcessData) {
            if (cachedRequestProcessData is CraftProcessRequestProcessData) {
                newRequestProcessData.recipe = cachedRequestProcessData.recipe
                newRequestProcessData.crafter = cachedRequestProcessData.crafter
                newRequestProcessData.sortedUserInventory = cachedRequestProcessData.sortedUserInventory
                newRequestProcessData.canBeStarted = cachedRequestProcessData.canBeStarted
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
                newRequestProcessData.visibleItems = cachedRequestProcessData.visibleItems
            }
        }
    }

}