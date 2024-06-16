package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteSkipRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge.ActionMergeHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.GodVoteSkipRequestMessage
import org.springframework.stereotype.Component

@Component
class GodVoteSkipMergeHandler : ActionMergeHandler {

    override fun accepts(type: String): Boolean = type == GodVoteSkipRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is GodVoteSkipRequestProcessData) {
            if (cachedRequestProcessData is GodVoteSkipRequestProcessData) {
                newRequestProcessData.canSkip = cachedRequestProcessData.canSkip
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
                newRequestProcessData.visibleItems = cachedRequestProcessData.visibleItems
            }
        }
    }

}