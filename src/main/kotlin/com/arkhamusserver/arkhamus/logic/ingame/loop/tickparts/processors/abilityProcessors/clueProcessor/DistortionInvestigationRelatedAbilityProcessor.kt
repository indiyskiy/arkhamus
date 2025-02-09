package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.clueProcessor

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameDistortionClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toAbility
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DistortionInvestigationRelatedAbilityProcessor(
    private val inGameDistortionClueRepository: InGameDistortionClueRepository
) : ActiveAbilityProcessor {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DistortionInvestigationRelatedAbilityProcessor::class.java)
    }

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.abilityId.toAbility()?.let { ability ->
            ability == Ability.ADVANCED_SEARCH_FOR_DISTORTION
        } == true
    }

    override fun processActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {
        val distortion = globalGameData.clues.distortion.first { it.stringId() == castAbility.targetId }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            distortion.castedAbilityUsers += user.inGameId()
            inGameDistortionClueRepository.save(distortion)
        }
    }

    override fun finishActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData) {
        val distortion = globalGameData.clues.distortion.first { it.stringId() == castAbility.targetId }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            distortion.castedAbilityUsers -= user.inGameId()
            inGameDistortionClueRepository.save(distortion)
        }
    }
}