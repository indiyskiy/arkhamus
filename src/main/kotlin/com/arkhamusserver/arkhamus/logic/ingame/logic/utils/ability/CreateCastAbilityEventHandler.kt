package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAbilityCastRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CreateCastAbilityEventHandler(
    private val inGameAbilityCastRepository: InGameAbilityCastRepository,
) {

    @Transactional
    fun createCastAbilityEvent(
        ability: Ability,
        sourceUserId: Long,
        gameId: Long,
        currentGameTime: Long,
        targetId: String? = null,
        targetType: GameObjectType? = null,
    ) {
        val abilityCast = InGameAbilityCast(
            id = generateRandomId(),
            gameId = gameId,
            ability = ability,
            sourceUserId = sourceUserId,
            targetId = targetId,
            targetType = targetType,
            timeStart = currentGameTime,
            timePast = 0,
            timeLeftActive = ability.active ?: 0L,
            timeLeftCooldown = setCooldown(ability),
            state = setState(ability),
            xLocation = null,
            yLocation = null,
        )
        inGameAbilityCastRepository.save(abilityCast)
    }

    private fun setCooldown(ability: Ability) =
        if (ability.cooldown >= (ability.active ?: 0L)) {
            ability.cooldown
        } else {
            ability.active ?: 0L
        }

    private fun setState(ability: Ability) =
        if ((ability.active ?: 0L) > 0) {
            InGameTimeEventState.ACTIVE
        } else {
            InGameTimeEventState.ON_COOLDOWN
        }

}