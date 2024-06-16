package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualPutItemRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge.ActionMergeHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.RitualPutItemRequestMessage
import org.springframework.stereotype.Component

@Component
class RitualPutItemMergeHandler : ActionMergeHandler {

    override fun accepts(type: String): Boolean = type == RitualPutItemRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is RitualPutItemRequestProcessData) {
            if (cachedRequestProcessData is RitualPutItemRequestProcessData) {
                newRequestProcessData.item = cachedRequestProcessData.item
                newRequestProcessData.itemNumber = cachedRequestProcessData.itemNumber
                newRequestProcessData.canPut = cachedRequestProcessData.canPut
                newRequestProcessData.currentGameTime = cachedRequestProcessData.currentGameTime
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
                newRequestProcessData.visibleItems = cachedRequestProcessData.visibleItems
            }
        }
    }

}