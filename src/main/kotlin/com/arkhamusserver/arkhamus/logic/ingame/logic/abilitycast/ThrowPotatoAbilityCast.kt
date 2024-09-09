package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.ItemHolderChangeType
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class ThrowPotatoAbilityCast(
    private val inventoryHandler: InventoryHandler,
    private val geometryUtils: GeometryUtils
) : AbilityCast {
    override fun accept(ability: Ability): Boolean {
        return ability == Ability.THROW_POTATO
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        throwPotato(globalGameData, abilityRequestProcessData)
        return true
    }

    private fun throwPotato(
        globalGameData: GlobalGameData,
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val currentUser = abilityRequestProcessData.gameUser
        currentUser?.let { currentUserNotNull ->
            val user = globalGameData.users.values.filter {
                it.userId != abilityRequestProcessData.gameUser.userId
            }.minByOrNull { user ->
                geometryUtils.distance(
                    currentUserNotNull,
                    user,
                )
            }
            if (user != null) {
                inventoryHandler.addItem(user, Item.CURSED_POTATO)
                rememberItemChangesForResponses(globalGameData, user)
            }
        }
    }

    private fun rememberItemChangesForResponses(
        globalGameData: GlobalGameData,
        user: RedisGameUser
    ) {
        globalGameData.inBetweenEvents.inBetweenItemHolderChanges.add(
            InBetweenItemHolderChanges(
                item = Item.CURSED_POTATO,
                number = 1,
                userId = user.userId,
                ItemHolderChangeType.TAKE
            )
        )
    }
}