package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.UpdateContainerGameData
import com.arkhamusserver.arkhamus.view.dto.netty.request.UpdateContainerRequestMessage
import org.springframework.stereotype.Component

@Component
class UpdateContainerMergeHandler(
    private val itemsMergingHandler: ItemsMergingHandler
) : ActionMergeHandler {

    override fun accepts(type: String) =
        type == UpdateContainerRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is UpdateContainerGameData) {
            if (cachedRequestProcessData is UpdateContainerGameData) {
                newRequestProcessData.sortedUserInventory = itemsMergingHandler.mergeItems(
                    newRequestProcessData.sortedUserInventory,
                    newRequestProcessData.visibleItems
                )
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
            }
        }
    }

}