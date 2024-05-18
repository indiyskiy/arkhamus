package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.view.dto.netty.request.AbilityRequestMessage
import org.springframework.stereotype.Component

@Component
class AbilityMergeHandler : ActionMergeHandler {

    override fun accepts(type: String): Boolean = type == AbilityRequestMessage::class.java.simpleName

    override fun merge(newRequestProcessData: GameUserData, cachedRequestProcessData: GameUserData) {
        if (newRequestProcessData is AbilityRequestProcessData) {
            if (cachedRequestProcessData is AbilityRequestProcessData) {
                newRequestProcessData.ability = cachedRequestProcessData.ability
                newRequestProcessData.canBeCasted = cachedRequestProcessData.canBeCasted
                newRequestProcessData.cooldown = cachedRequestProcessData.cooldown
                newRequestProcessData.cooldownOf = cachedRequestProcessData.cooldownOf
                newRequestProcessData.executedSuccessfully = cachedRequestProcessData.executedSuccessfully
                newRequestProcessData.item = cachedRequestProcessData.item
                newRequestProcessData.visibleItems = cachedRequestProcessData.visibleItems
            }
        }
    }

}