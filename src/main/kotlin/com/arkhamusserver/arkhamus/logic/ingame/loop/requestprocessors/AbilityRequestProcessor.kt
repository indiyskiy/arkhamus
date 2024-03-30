package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.logic.AbilityCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.UserInventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.view.dto.netty.request.AbilityRequestMessage
import org.springframework.stereotype.Component

@Component
class AbilityRequestProcessor(
    private val userInventoryHandler: UserInventoryHandler,
    private val abilityCastHandler: AbilityCastHandler
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageContainer): Boolean {
        return request.nettyRequestMessage is AbilityRequestMessage
    }

    override fun process(
        requestContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val abilityRequestProcessData = requestContainer.requestProcessData as AbilityRequestProcessData
        val ability = abilityRequestProcessData.ability
        if (ability != null) {
            val canBeCasted = abilityRequestProcessData.canBeCasted
            if (canBeCasted) {
                val item = abilityRequestProcessData.item
                if (item != null) {
                    if (ability.isConsumesItem()) {
                        userInventoryHandler.consumeItem(
                            abilityRequestProcessData.gameUser!!,
                            item
                        )
                    }
                    abilityCastHandler.cast(ability, abilityRequestProcessData, globalGameData)
                    abilityRequestProcessData.castedSuccessfully = true
                }
            }
        }
    }
}