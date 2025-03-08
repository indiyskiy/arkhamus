package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.classbased.cultist

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorTag
import com.arkhamusserver.arkhamus.model.ingame.InGameDoor
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component

@Component
class LockDoorAbilityCondition(
    private val geometryUtils: GeometryUtils,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == Ability.PARALYSE
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) return false
        val targetDoor = (target as? InGameDoor) ?: return false
        return geometryUtils.distanceLessOrEquals(user, targetDoor, ability.range) &&
                !targetDoor.additionalTags.contains(DoorTag.CLOSED_BY_ABILITY) &&
                !targetDoor.additionalTags.contains(DoorTag.CLOSED_BY_DESIGN) &&
                !targetDoor.additionalTags.contains(DoorTag.CLOSED_IN_GAME_FOREVER) &&
                targetDoor.additionalTags.contains(DoorTag.OPEN_SOMETIMES) &&
                targetDoor.globalState == DoorState.OPEN
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: InGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return gameObjectFinder.all(
            ability.targetTypes ?: emptyList(),
            globalGameData
        ).any { targetDoor ->
            targetDoor is InGameDoor &&
                    geometryUtils.distanceLessOrEquals(user, targetDoor, ability.range) &&
                    !targetDoor.additionalTags.contains(DoorTag.CLOSED_BY_ABILITY) &&
                    !targetDoor.additionalTags.contains(DoorTag.CLOSED_BY_DESIGN) &&
                    !targetDoor.additionalTags.contains(DoorTag.CLOSED_IN_GAME_FOREVER) &&
                    targetDoor.additionalTags.contains(DoorTag.OPEN_SOMETIMES) &&
                    targetDoor.globalState == DoorState.OPEN
        }
    }
}