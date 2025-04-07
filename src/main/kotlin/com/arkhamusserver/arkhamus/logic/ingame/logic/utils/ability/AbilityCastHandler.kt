package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.aftershock.CastAftershockHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import org.springframework.stereotype.Component

@Component
class AbilityCastHandler(
    private val abilityCasts: List<AbilityCast>,
    private val inventoryHandler: InventoryHandler,
    private val createCastAbilityEventHandler: CreateCastAbilityEventHandler,
    private val shortTimeEventHandler: ShortTimeEventHandler,
    private val abilityToItemResolver: AbilityToItemResolver,
    private val activityHandler: ActivityHandler,
    private val castAftershockHandlers: List<CastAftershockHandler>
) {
    fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData,
    ): Boolean {
        val item: Item? = abilityToItemResolver.resolve(ability)
        val casted = abilityCasts
            .first { it.accept(ability) }
            .cast(ability, abilityRequestProcessData, globalGameData)
        processCastedSuccess(
            casted,
            item,
            ability,
            abilityRequestProcessData.target,
            abilityRequestProcessData.gameUser!!,
            globalGameData.game.inGameId(),
            globalGameData,
            abilityRequestProcessData.targetType
        )
        if (casted) {
            createActivity(
                gameId = globalGameData.game.inGameId(),
                sourceUser = abilityRequestProcessData.gameUser,
                gameTime = globalGameData.game.globalTimer,
                relatedGameObjectType = abilityRequestProcessData.targetType,
                relatedGameObject = if (abilityRequestProcessData.target != null && abilityRequestProcessData.target is WithTrueIngameId) {
                    abilityRequestProcessData.target as WithTrueIngameId
                } else {
                    null
                },
                ability = ability
            )
            processCastAftershocks(
                ability,
                abilityRequestProcessData.gameUser,
                abilityRequestProcessData.target,
                globalGameData
            )
        }
        return casted
    }

    fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData,
        targetType: GameObjectType? = null,
    ): Boolean {
        val casted = abilityCasts
            .first { it.accept(ability) }
            .cast(sourceUser, ability, target, globalGameData)
        val item: Item? = abilityToItemResolver.resolve(ability)

        processCastedSuccess(
            casted,
            item,
            ability,
            target,
            sourceUser,
            globalGameData.game.inGameId(),
            globalGameData,
            targetType
        )
        if (casted) {
            createActivity(
                gameId = globalGameData.game.inGameId(),
                sourceUser = sourceUser,
                gameTime = globalGameData.game.globalTimer,
                relatedGameObjectType = targetType,
                relatedGameObject = if (target != null && target is WithTrueIngameId) {
                    target
                } else {
                    null
                },
                ability = ability
            )
        }
        return casted
    }

    private fun consumeItem(
        ability: Ability,
        gameUser: InGameUser,
        item: Item
    ) {
        if (ability.consumesItem) {
            inventoryHandler.consumeItem(
                gameUser,
                item
            )
        }
    }

    private fun processCastedSuccess(
        casted: Boolean,
        item: Item?,
        ability: Ability,
        target: WithStringId?,
        userAccount: InGameUser,
        gameId: Long,
        globalGameData: GlobalGameData,
        targetType: GameObjectType? = null,
    ) {
        if (casted) {
            if (item != null && ability.consumesItem) {
                consumeItem(ability, userAccount, item)
            }
            createCastAbility(
                ability,
                userAccount.inGameId(),
                gameId,
                globalGameData.game.globalTimer,
                target?.stringId(),
                targetType,
                globalGameData
            )
        }
    }

    private fun createCastAbility(
        ability: Ability,
        userId: Long,
        gameId: Long,
        globalTimer: Long,
        targetId: String?,
        targetType: GameObjectType?,
        data: GlobalGameData
    ) {
        createCastAbilityEventHandler.createCastAbilityEvent(
            ability = ability,
            sourceUserId = userId,
            gameId = gameId,
            currentGameTime = globalTimer,
            targetId = targetId,
            targetType = targetType
        )
        shortTimeEventHandler.createShortTimeEvent(
            objectId = userId,
            gameId = gameId,
            globalTimer = globalTimer,
            type = ShortTimeEventType.ABILITY_CAST,
            visibilityModifiers = ability.visibilityModifiers(),
            data = data,
            sourceUserId = userId
        )
    }

    private fun createActivity(
        gameId: Long,
        sourceUser: InGameUser,
        gameTime: Long,
        relatedGameObjectType: GameObjectType?,
        relatedGameObject: WithTrueIngameId?,
        ability: Ability,
    ) {
        activityHandler.addUserWithTargetActivity(
            gameId,
            ActivityType.ABILITY_CASTED,
            sourceUser,
            gameTime,
            relatedGameObjectType,
            relatedGameObject,
            ability.id.toLong(),
        )
    }

    private fun processCastAftershocks(
        ability: Ability,
        user: InGameUser,
        target: WithStringId?,
        data: GlobalGameData
    ) {
        castAftershockHandlers.filter {
            it.accept(ability)
        }.forEach {
            it.processCastAftershocks(ability, user, target, data)
        }
    }
}