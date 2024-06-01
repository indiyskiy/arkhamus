package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.logic.ingame.logic.CanAbilityBeCastedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.RelatedAbilityCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.AbilityRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class AbilityNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val abilityToItemResolver: AbilityToItemResolver,
    private val relatedAbilityCastHandler: RelatedAbilityCastHandler,
    private val canAbilityBeCastedHandler: CanAbilityBeCastedHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler
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
                buildAbilityGameData(
                    ability,
                    canBeCasted,
                    relatedAbility,
                    requiredItem,
                    user,
                    users,
                    ongoingEvents,
                    globalGameData
                )
            } ?: buildWrongAbilityGameData(user, users, ongoingEvents, globalGameData)
        }
    }

    private fun buildAbilityGameData(
        ability: Ability,
        canBeCasted: Boolean,
        relatedAbility: RedisAbilityCast?,
        requiredItem: Item?,
        user: RedisGameUser,
        users: List<RedisGameUser>,
        ongoingEvents: List<OngoingEvent>,
        globalGameData: GlobalGameData
    ) = AbilityRequestProcessData(
        ability = ability,
        canBeCasted = canBeCasted,
        cooldown = relatedAbility?.timeLeft,
        cooldownOf = ability.cooldown,
        item = requiredItem,
        executedSuccessfully = false,
        gameUser = user,
        otherGameUsers = users,
        visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
        availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
        visibleItems = inventoryHandler.mapUsersItems(user.items),
        ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
            user,
            globalGameData.crafters,
            globalGameData.craftProcess
        ),
        containers = globalGameData.containers.values.toList(),
        tick = globalGameData.game.currentTick
    )


    private fun buildWrongAbilityGameData(
        user: RedisGameUser,
        users: List<RedisGameUser>,
        ongoingEvents: List<OngoingEvent>,
        globalGameData: GlobalGameData
    ) = AbilityRequestProcessData(
        ability = null,
        canBeCasted = false,
        cooldown = null,
        cooldownOf = null,
        item = null,
        executedSuccessfully = false,
        gameUser = user,
        otherGameUsers = users,
        visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
        availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
        ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
            user,
            globalGameData.crafters,
            globalGameData.craftProcess
        ),
        visibleItems = inventoryHandler.mapUsersItems(user.items),
        containers = globalGameData.containers.values.toList(),
        tick = globalGameData.game.currentTick
    )
}
