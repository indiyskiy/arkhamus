package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.clueProcessor

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameOmenClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toAbility
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OmenInvestigationRelatedAbilityProcessor(
    private val inGameOmenClueRepository: InGameOmenClueRepository
) : ActiveAbilityProcessor {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OmenInvestigationRelatedAbilityProcessor::class.java)
    }

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.abilityId.toAbility()?.let { ability ->
            ability == Ability.SEARCH_FOR_OMEN
        } == true
    }

    override fun processActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {
        val omen = globalGameData.clues.omen.first { it.stringId() == castAbility.targetId }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            omen.castedAbilityUsers += user.inGameId()
            inGameOmenClueRepository.save(omen)
        }
    }

    override fun finishActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData) {
        val omen = globalGameData.clues.omen.first { it.stringId() == castAbility.targetId }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            omen.castedAbilityUsers -= user.inGameId()
            inGameOmenClueRepository.save(omen)
        }
    }
}