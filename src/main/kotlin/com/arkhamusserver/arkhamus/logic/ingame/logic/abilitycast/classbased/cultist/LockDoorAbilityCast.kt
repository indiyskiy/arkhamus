package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.classbased.cultist

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameDoorRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorTag
import com.arkhamusserver.arkhamus.model.ingame.InGameDoor
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class LockDoorAbilityCast(
    private val inGameDoorRepository: InGameDoorRepository
) : AbilityCast {

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.PARALYSE
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        lockDoor(abilityRequestProcessData)
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        lockDoor(target as InGameDoor)
        return true
    }

    private fun lockDoor(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val targetDoor = abilityRequestProcessData.target as InGameDoor
        lockDoor(targetDoor)
    }

    private fun lockDoor(
        targetDoor: InGameDoor,
    ) {
        targetDoor.additionalTags += DoorTag.CLOSED_BY_ABILITY
        targetDoor.globalState = DoorState.CLOSED
        inGameDoorRepository.save(targetDoor)
    }
}