package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.CanAbilityBeCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.RelatedAbilityCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.view.dto.netty.request.AbilityRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.slf4j.LoggerFactory
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
    private val questProgressHandler: QuestProgressHandler,
    private val finder: GameObjectFinder
) : NettyRequestHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(AbilityNettyRequestHandler::class.java)
    }

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == AbilityRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>,
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
            val users = globalGameData.users.values.filter { it.inGameId() != userId }
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                inZones,
                user
            )
            return ability?.let {
                val relatedAbility =
                    relatedAbilityCastHandler.findForUser(user, ability, globalGameData.castAbilities)
                val requiredItem = abilityToItemResolver.resolve(it)
                val canUserSeeAbility = canAbilityBeCastHandler.canUserSeeAbility(user, ability, requiredItem)
                val target = if (targetId != null && targetType != null) {
                    finder.findById(
                        targetId,
                        targetType,
                        globalGameData
                    )
                } else null
                val canUserCastAbility = canAbilityBeCastHandler.canBeCastedRightNow(
                    ability,
                    user,
                    target,
                    globalGameData
                )
                logger.info("canUserSeeAbility: $canUserSeeAbility, canUserCastAbility: $canUserCastAbility")
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
                    clues,
                    target,
                    targetType,
                )
            } ?: buildWrongAbilityGameData(
                user,
                users,
                ongoingEvents,
                inZones,
                globalGameData,
                clues
            )
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
        clues: List<RedisClue>,
        target: WithStringId?,
        targetType: GameObjectType?,
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
        tick = globalGameData.game.currentTick,
        userQuestProgresses = questProgressHandler.mapQuestProgresses(
            globalGameData.questProgressByUserId,
            user,
            globalGameData.quests
        ),
        target = target,
        targetType = targetType,
    )


    private fun buildWrongAbilityGameData(
        user: RedisGameUser,
        users: List<RedisGameUser>,
        ongoingEvents: List<OngoingEvent>,
        inZones: List<LevelZone>,
        globalGameData: GlobalGameData,
        clues: List<RedisClue>,
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
        tick = globalGameData.game.currentTick,
        userQuestProgresses = questProgressHandler.mapQuestProgresses(
            globalGameData.questProgressByUserId,
            user,
            globalGameData.quests
        ),
        target = null,
        targetType = null,
    )
}
