package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.AbilityCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import org.springframework.stereotype.Component

@Component
class AbilityRequestProcessor(
    private val inventoryHandler: InventoryHandler,
    private val abilityCastHandler: AbilityCastHandler,
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is AbilityRequestProcessData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val abilityRequestProcessData = requestDataHolder.requestProcessData as AbilityRequestProcessData
        val ability = abilityRequestProcessData.ability
        if (ability != null) {
            val canBeCast =
                abilityRequestProcessData.canBeSeen &&
                        abilityRequestProcessData.fitAdditionalConditions &&
                        (abilityRequestProcessData.cooldown?.let { it <= 0 } ?: true)
            if (canBeCast) {
                val item = abilityRequestProcessData.item
                if (item != null && ability.consumesItem) {
                    consumeItem(ability, abilityRequestProcessData, item)
                }
                abilityCastHandler.cast(ability, abilityRequestProcessData, globalGameData)
                createCastAbility(
                    ability,
                    abilityRequestProcessData,
                    requestDataHolder.userAccount.id!!,
                    requestDataHolder.gameSession!!.id!!,
                    globalGameData.game.globalTimer
                )
                abilityRequestProcessData.executedSuccessfully = true

            }
        }
    }

    private fun createCastAbility(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        id: Long,
        gameId: Long,
        globalTimer: Long
    ) {
        abilityCastHandler.createCastAbilityEvent(ability, abilityRequestProcessData, id, gameId, globalTimer)
    }

    private fun consumeItem(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        item: Item
    ) {
        if (ability.consumesItem) {
            inventoryHandler.consumeItem(
                abilityRequestProcessData.gameUser!!,
                item
            )
        }
    }
}