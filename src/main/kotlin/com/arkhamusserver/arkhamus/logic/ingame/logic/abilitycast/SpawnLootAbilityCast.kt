package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.ItemHolderChangeType
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class SpawnLootAbilityCast(
    private val inventoryHandler: InventoryHandler,
) : AbilityCast {

    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SPAWN_LOOT
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        spawnLoot(globalGameData, abilityRequestProcessData)
        return true
    }

    private fun spawnLoot(
        globalGameData: GlobalGameData,
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val currentUser = abilityRequestProcessData.gameUser
        currentUser?.let { currentUserNotNull ->
            val randomItem = Item.values()
                .filter { item -> item.itemType == ItemType.LOOT }
                .random(random)
            inventoryHandler.addItem(currentUserNotNull, randomItem)
            rememberItemChangesForResponses(globalGameData, currentUserNotNull)
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
