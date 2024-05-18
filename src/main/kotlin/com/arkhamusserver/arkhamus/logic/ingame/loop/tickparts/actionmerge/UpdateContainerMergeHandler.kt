package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.UpdateContainerGameData
import com.arkhamusserver.arkhamus.view.dto.netty.request.UpdateContainerRequestMessage
import org.springframework.stereotype.Component

@Component
class UpdateContainerMergeHandler : ActionMergeHandler {

    override fun accepts(type: String) =
        type == UpdateContainerRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is UpdateContainerGameData) {
            if (cachedRequestProcessData is UpdateContainerGameData) {
                newRequestProcessData.container = cachedRequestProcessData.container
                newRequestProcessData.sortedUserInventory = cachedRequestProcessData.sortedUserInventory
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
                newRequestProcessData.visibleItems = cachedRequestProcessData.visibleItems
            }
        }
    }

}