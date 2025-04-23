package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability

import com.arkhamusserver.arkhamus.logic.globalUtils.TimeBaseCalculator
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
    private val calculator: TimeBaseCalculator,
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
        val active = calculator.resolveAbilityActive(ability) ?: 0L
        val abilityCast = InGameAbilityCast(
            id = generateRandomId(),
            gameId = gameId,
            ability = ability,
            sourceUserId = sourceUserId,
            targetId = targetId,
            targetType = targetType,
            timeStart = currentGameTime,
            timePast = 0,
            timeLeftCooldown = calculator.resolveAbilityCooldown(ability),
            timeLeftActive = active,
            state = setState(active),
            xLocation = null,
            yLocation = null,
        )
        inGameAbilityCastRepository.save(abilityCast)
    }


    private fun setState(active: Long) =
        if (active > 0L) {
            InGameTimeEventState.ACTIVE
        } else {
            InGameTimeEventState.ON_COOLDOWN
        }

}