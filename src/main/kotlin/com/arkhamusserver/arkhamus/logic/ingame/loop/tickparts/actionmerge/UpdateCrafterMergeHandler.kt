package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.UpdateCrafterGameData
import com.arkhamusserver.arkhamus.view.dto.netty.request.UpdateCrafterRequestMessage
import org.springframework.stereotype.Component

@Component
class UpdateCrafterMergeHandler : ActionMergeHandler {

    override fun accepts(type: String): Boolean = type == UpdateCrafterRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is UpdateCrafterGameData) {
            if (cachedRequestProcessData is UpdateCrafterGameData) {
                newRequestProcessData.crafter = cachedRequestProcessData.crafter
                newRequestProcessData.sortedUserInventory = cachedRequestProcessData.sortedUserInventory
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
                newRequestProcessData.visibleItems = cachedRequestProcessData.visibleItems
            }
        }
    }

}