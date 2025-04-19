package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.cultist

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameDoorRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorTag
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import com.arkhamusserver.arkhamus.model.ingame.InGameDoor
import org.springframework.stereotype.Component

@Component
class LockDoorAbilityProcessor(
    val finder: GameObjectFinder,
    val inGameDoorRepository: InGameDoorRepository
) : ActiveAbilityProcessor {

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.ability == Ability.LOCK_DOOR
    }

    override fun processActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {
        val targetId = castAbility.targetId
        val targetType = castAbility.targetType
        if (targetId == null || targetType == null) return
        val target = finder.findById(targetId, targetType, globalGameData)
        if (target == null) return
        if (target is InGameDoor) {
            if (target.globalState == DoorState.CLOSED &&
                DoorTag.CLOSED_BY_ABILITY in target.additionalTags &&
                DoorTag.OPEN_SOMETIMES in target.additionalTags
            ) {
                target.globalState = DoorState.OPEN
                target.additionalTags -= DoorTag.CLOSED_BY_ABILITY
                inGameDoorRepository.save(target)
            }
        }
    }

}


