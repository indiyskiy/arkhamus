package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.THROW_POTATO
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component

@Component
class ThrowPotatoCondition(
    private val gameObjectFinder: GameObjectFinder,
    private val userLocationHandler: UserLocationHandler,
    private val inventoryHandler: InventoryHandler,
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == THROW_POTATO
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        return target != null &&
                target is InGameUser &&
                target.inGameId() != user.inGameId() &&
                userLocationHandler.userCanSeeTargetInRange(
                    user,
                    target,
                    globalGameData.levelGeometryData,
                    ability.range ?: 0.0,
                    true
                ) &&
                inventoryHandler.itemCanBeAdded(target, Item.CURSED_POTATO)
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: InGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return gameObjectFinder.all(
            ability.targetTypes ?: emptyList(),
            globalGameData
        ).any {
            canBeCastedRightNow(
                ability,
                user,
                it,
                globalGameData
            )
        }
    }
}