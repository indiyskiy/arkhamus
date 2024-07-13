package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.*
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.AbilityRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class AbilityNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val abilityToItemResolver: AbilityToItemResolver,
    private val relatedAbilityCastHandler: RelatedAbilityCastHandler,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val zonesHandler: ZonesHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val clueHandler: ClueHandler,
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
            val inZones = zonesHandler.filterByPosition(
                requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
                globalGameData.levelGeometryData
            )
            val ability = Ability.byId(this.abilityId)
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                inZones,
                globalGameData.castAbilities,
                userId!!
            )
            return ability?.let {
                val relatedAbility =
                    relatedAbilityCastHandler.findForUser(user, ability, globalGameData.castAbilities)
                val requiredItem = abilityToItemResolver.resolve(it)
                val canUserSeeAbility = canAbilityBeCastHandler.canUserSeeAbility(user, ability, requiredItem)
                val canUserCastAbility = canAbilityBeCastHandler.fitAdditionalCondition(ability, user, globalGameData)
                buildAbilityGameData(
                    ability,
                    canUserSeeAbility,
                    canUserCastAbility,
                    relatedAbility,
                    requiredItem,
                    user,
                    users,
                    inZones,
                    ongoingEvents,
                    globalGameData,
                    clues
                )
            } ?: buildWrongAbilityGameData(user, users, ongoingEvents, inZones, globalGameData, clues)
        }
    }

    private fun buildAbilityGameData(
        ability: Ability,
        canBeSeen: Boolean,
        canBeCast: Boolean,
        relatedAbility: RedisAbilityCast?,
        requiredItem: Item?,
        user: RedisGameUser,
        users: List<RedisGameUser>,
        inZones: List<LevelZone>,
        ongoingEvents: List<OngoingEvent>,
        globalGameData: GlobalGameData,
        clues: List<RedisClue>
    ) = AbilityRequestProcessData(
        ability = ability,
        canBeSeen = canBeSeen,
        fitAdditionalConditions = canBeCast,
        cooldown = relatedAbility?.timeLeftCooldown,
        cooldownOf = ability.cooldown,
        item = requiredItem,
        executedSuccessfully = false,
        gameUser = user,
        otherGameUsers = users,
        inZones = inZones,
        visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
        availableAbilities = canAbilityBeCastHandler.abilityOfUserResponses(user, globalGameData),
        visibleItems = inventoryHandler.mapUsersItems(user.items),
        ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
            user,
            globalGameData.crafters,
            globalGameData.craftProcess
        ),
        containers = globalGameData.containers.values.toList(),
        crafters = globalGameData.crafters.values.toList(),
        clues = clues,
        tick = globalGameData.game.currentTick
    )


    private fun buildWrongAbilityGameData(
        user: RedisGameUser,
        users: List<RedisGameUser>,
        ongoingEvents: List<OngoingEvent>,
        inZones: List<LevelZone>,
        globalGameData: GlobalGameData,
        clues: List<RedisClue>
    ) = AbilityRequestProcessData(
        ability = null,
        canBeSeen = false,
        fitAdditionalConditions = false,
        cooldown = null,
        cooldownOf = null,
        item = null,
        executedSuccessfully = false,
        gameUser = user,
        otherGameUsers = users,
        inZones = inZones,
        visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
        availableAbilities = canAbilityBeCastHandler.abilityOfUserResponses(user, globalGameData),
        ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
            user,
            globalGameData.crafters,
            globalGameData.craftProcess
        ),
        visibleItems = inventoryHandler.mapUsersItems(user.items),
        containers = globalGameData.containers.values.toList(),
        crafters = globalGameData.crafters.values.toList(),
        clues = clues,
        tick = globalGameData.game.currentTick
    )
}
