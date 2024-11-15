package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class AbilityCastHandler(
    private val abilityCasts: List<AbilityCast>,
    private val inventoryHandler: InventoryHandler,
    private val createCastAbilityEventHandler: CreateCastAbilityEventHandler,
    private val shortTimeEventHandler: ShortTimeEventHandler,
    private val abilityToItemResolver: AbilityToItemResolver
) {
    fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData,
    ): Boolean {
        val casted = abilityCasts
            .first { it.accept(ability) }
            .cast(ability, abilityRequestProcessData, globalGameData)
        val item: Item? = abilityToItemResolver.resolve(ability)
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
        return casted
    }

    fun cast(
        sourceUser: RedisGameUser,
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
        return casted
    }

    private fun consumeItem(
        ability: Ability,
        gameUser: RedisGameUser,
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
        userAccount: RedisGameUser,
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
            userId,
            gameId,
            globalTimer,
            ShortTimeEventType.ABILITY_CAST,
            ability.visibilityModifiers(),
            data
        )
    }
}