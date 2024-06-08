package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GodVoteSkipRequestProcessData
import com.arkhamusserver.arkhamus.view.dto.netty.request.GodVoteSkipRequestMessage
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