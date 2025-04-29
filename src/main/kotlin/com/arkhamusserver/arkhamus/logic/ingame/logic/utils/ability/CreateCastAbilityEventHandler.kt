package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability

import com.arkhamusserver.arkhamus.logic.globalUtils.TimeBaseCalculator
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAbilityActiveCastRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAbilityCooldownRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityActiveCast
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCooldown
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CreateCastAbilityEventHandler(
    private val inGameAbilityActiveCastRepository: InGameAbilityActiveCastRepository,
    private val inGameAbilityCooldownRepository: InGameAbilityCooldownRepository,
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
        buildCooldown(gameId, ability, sourceUserId, targetId, targetType, currentGameTime)
        buildActiveTime(ability, gameId, sourceUserId, targetId, targetType, currentGameTime)
    }

    private fun buildActiveTime(
        ability: Ability,
        gameId: Long,
        sourceUserId: Long,
        targetId: String?,
        targetType: GameObjectType?,
        currentGameTime: Long
    ) {
        val active = calculator.resolveAbilityActive(ability)
        if (active != null && active > 0) {
            val abilityCast = InGameAbilityActiveCast(
                id = generateRandomId(),
                gameId = gameId,
                ability = ability,
                sourceUserId = sourceUserId,
                targetId = targetId,
                targetType = targetType,
                timeStart = currentGameTime,
                timePast = 0,
                timeLeftActive = active,
                state = InGameTimeEventState.ACTIVE,
                xLocation = null,
                yLocation = null,
            )
            inGameAbilityActiveCastRepository.save(abilityCast)
        }
    }

    private fun buildCooldown(
        gameId: Long,
        ability: Ability,
        sourceUserId: Long,
        targetId: String?,
        targetType: GameObjectType?,
        currentGameTime: Long
    ) {
        val abilityCooldown = InGameAbilityCooldown(
            id = generateRandomId(),
            gameId = gameId,
            ability = ability,
            sourceUserId = sourceUserId,
            targetId = targetId,
            targetType = targetType,
            timeStart = currentGameTime,
            timePast = 0,
            timeLeftCooldown = calculator.resolveAbilityCooldown(ability),
            state = InGameTimeEventState.ON_COOLDOWN,
            xLocation = null,
            yLocation = null,
        )
        inGameAbilityCooldownRepository.save(abilityCooldown)
    }

}