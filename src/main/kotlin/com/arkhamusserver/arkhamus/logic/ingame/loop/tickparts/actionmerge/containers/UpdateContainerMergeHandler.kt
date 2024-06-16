package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge.containers

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container.UpdateContainerRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge.ActionMergeHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge.ItemsMergingHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.container.UpdateContainerRequestMessage
import org.springframework.stereotype.Component

@Component
class UpdateContainerMergeHandler(
    private val itemsMergingHandler: ItemsMergingHandler
) : ActionMergeHandler {

    override fun accepts(type: String) =
        type == UpdateContainerRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is UpdateContainerRequestGameData) {
            if (cachedRequestProcessData is UpdateContainerRequestGameData) {
                newRequestProcessData.sortedUserInventory = itemsMergingHandler.mergeItems(
                    newRequestProcessData.sortedUserInventory,
                    newRequestProcessData.visibleItems
                )
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
            }
        }
    }

}