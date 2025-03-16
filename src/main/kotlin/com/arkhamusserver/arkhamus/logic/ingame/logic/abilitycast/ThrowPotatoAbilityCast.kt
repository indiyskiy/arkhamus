package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.ItemHolderChangeType
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class ThrowPotatoAbilityCast(
    private val inventoryHandler: InventoryHandler,
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

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        throwPotatoAtUser(target as InGameUser, globalGameData)
        return true
    }

    private fun throwPotato(
        globalGameData: GlobalGameData,
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val targetUser = abilityRequestProcessData.target as InGameUser
        throwPotatoAtUser(targetUser, globalGameData)
    }

    private fun throwPotatoAtUser(
        targetUser: InGameUser,
        globalGameData: GlobalGameData
    ) {
        if (targetUser.stateTags.contains(UserStateTag.INVULNERABILITY)) return
        if(!inventoryHandler.itemCanBeAdded(targetUser, Item.CURSED_POTATO)) return
        inventoryHandler.addItem(targetUser, Item.CURSED_POTATO)
        rememberItemChangesForResponses(globalGameData, targetUser)
    }

    private fun rememberItemChangesForResponses(
        globalGameData: GlobalGameData,
        user: InGameUser
    ) {
        globalGameData.inBetweenEvents.inBetweenItemHolderChanges.add(
            InBetweenItemHolderChanges(
                item = Item.CURSED_POTATO,
                number = 1,
                userId = user.inGameId(),
                ItemHolderChangeType.TAKE
            )
        )
    }
}