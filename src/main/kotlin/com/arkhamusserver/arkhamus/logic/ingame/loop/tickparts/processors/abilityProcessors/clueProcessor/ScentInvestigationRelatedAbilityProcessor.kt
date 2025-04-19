package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.clueProcessor

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameScentClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.springframework.stereotype.Component

@Component
class ScentInvestigationRelatedAbilityProcessor(
    private val inGameScentClueRepository: InGameScentClueRepository
) : ActiveAbilityProcessor {

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.ability == Ability.SEARCH_FOR_SCENT
    }

    override fun processActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {
        val scent = globalGameData.clues.scent.first { it.stringId() == castAbility.targetId }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            scent.castedAbilityUsers += user.inGameId()
            inGameScentClueRepository.save(scent)
        }
    }

    override fun finishActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData) {
        val scent = globalGameData.clues.scent.first { it.stringId() == castAbility.targetId }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            scent.castedAbilityUsers -= user.inGameId()
            inGameScentClueRepository.save(scent)
        }
    }
}