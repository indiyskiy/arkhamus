package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.classbased.investigator

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.ItemHolderChangeType
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
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

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        spawnLoot(sourceUser, globalGameData)
        return true
    }

    private fun spawnLoot(
        globalGameData: GlobalGameData,
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val currentUser = abilityRequestProcessData.gameUser
        currentUser?.let { currentUserNotNull ->
            spawnLoot(currentUserNotNull, globalGameData)
        }
    }

    private fun spawnLoot(
        currentUserNotNull: InGameUser,
        globalGameData: GlobalGameData
    ) {
        val randomItem = Item.values()
            .filter { item -> item.itemType == ItemType.LOOT }
            .random(random)
        if(!inventoryHandler.itemCanBeAdded(currentUserNotNull, randomItem)) return
        inventoryHandler.addItem(currentUserNotNull, randomItem)
        rememberItemChangesForResponses(globalGameData, currentUserNotNull, randomItem)
    }

    private fun rememberItemChangesForResponses(
        globalGameData: GlobalGameData,
        user: InGameUser,
        spawnedItem: Item,
    ) {
        globalGameData.inBetweenEvents.inBetweenItemHolderChanges.add(
            InBetweenItemHolderChanges(
                item = spawnedItem,
                number = 1,
                userId = user.inGameId(),
                ItemHolderChangeType.TAKE
            )
        )
    }
}