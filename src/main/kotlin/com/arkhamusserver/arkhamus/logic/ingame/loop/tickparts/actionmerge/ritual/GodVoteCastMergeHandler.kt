package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteCastRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge.ActionMergeHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.GodVoteCastRequestMessage
import org.springframework.stereotype.Component

@Component
class GodVoteCastMergeHandler : ActionMergeHandler {

    override fun accepts(type: String): Boolean = type == GodVoteCastRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is GodVoteCastRequestProcessData) {
            if (cachedRequestProcessData is GodVoteCastRequestProcessData) {
                newRequestProcessData.votedGod = cachedRequestProcessData.votedGod
                newRequestProcessData.canVote = cachedRequestProcessData.canVote
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
                newRequestProcessData.visibleItems = cachedRequestProcessData.visibleItems
            }
        }
    }

}