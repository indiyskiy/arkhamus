package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.UpdateCrafterRequestGameData
import com.arkhamusserver.arkhamus.view.dto.netty.request.UpdateCrafterRequestMessage
import org.springframework.stereotype.Component

@Component
class UpdateCrafterMergeHandler(
    private val itemsMergingHandler: ItemsMergingHandler
) : ActionMergeHandler {

    override fun accepts(type: String): Boolean = type == UpdateCrafterRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is UpdateCrafterRequestGameData) {
            if (cachedRequestProcessData is UpdateCrafterRequestGameData) {
                newRequestProcessData.sortedUserInventory = itemsMergingHandler.mergeItems(
                    newRequestProcessData.sortedUserInventory,
                    newRequestProcessData.visibleItems
                )
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
            }
        }
    }

}