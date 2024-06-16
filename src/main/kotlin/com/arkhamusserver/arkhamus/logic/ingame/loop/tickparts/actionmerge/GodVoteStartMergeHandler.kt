package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteStartRequestProcessData
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.GodVoteStartRequestMessage
import org.springframework.stereotype.Component

@Component
class GodVoteStartMergeHandler : ActionMergeHandler {

    override fun accepts(type: String): Boolean = type == GodVoteStartRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is GodVoteStartRequestProcessData) {
            if (cachedRequestProcessData is GodVoteStartRequestProcessData) {
                newRequestProcessData.votedGod = cachedRequestProcessData.votedGod
                newRequestProcessData.canBeStarted = cachedRequestProcessData.canBeStarted
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
                newRequestProcessData.visibleItems = cachedRequestProcessData.visibleItems
            }
        }
    }

}