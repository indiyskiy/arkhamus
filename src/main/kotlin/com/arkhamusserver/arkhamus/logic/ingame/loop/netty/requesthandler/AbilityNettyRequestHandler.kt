package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.logic.ingame.logic.CanAbilityBeCastedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.RelatedAbilityCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.UserInventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.view.dto.netty.request.AbilityRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class AbilityNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val abilityToItemResolver: AbilityToItemResolver,
    private val relatedAbilityCastHandler: RelatedAbilityCastHandler,
    private val canAbilityBeCastedHandler: CanAbilityBeCastedHandler,
    private val inventoryHandler: UserInventoryHandler
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == AbilityRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): RequestProcessData {
        val userId = requestDataHolder.userAccount.id
        val request = requestDataHolder.nettyRequestMessage
        with(request as AbilityRequestMessage) {
            val ability = Ability.byId(request.abilityId)
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            return ability?.let {
                val relatedAbility =
                    relatedAbilityCastHandler.findForUser(user, ability, globalGameData.castedAbilities)
                val requiredItem = abilityToItemResolver.resolve(it)
                val canBeCasted = canAbilityBeCastedHandler.canUserCast(user, ability, requiredItem)
                AbilityRequestProcessData(
                    ability = ability,
                    canBeCasted = canBeCasted,
                    cooldown = relatedAbility?.timeLeft,
                    cooldownOf = ability.cooldown,
                    item = requiredItem,
                    castedSuccessfully = false,
                    gameUser = user,
                    otherGameUsers = users,
                    visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                    availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                    visibleItems = inventoryHandler.mapUsersItems(user.items),
                    tick = globalGameData.game.currentTick
                )
            } ?: AbilityRequestProcessData(
                ability = null,
                canBeCasted = false,
                cooldown = null,
                cooldownOf = null,
                item = null,
                castedSuccessfully = false,
                gameUser = user,
                otherGameUsers = users,
                visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                visibleItems = inventoryHandler.mapUsersItems(user.items),
                tick = globalGameData.game.currentTick
            )
        }
    }
}
