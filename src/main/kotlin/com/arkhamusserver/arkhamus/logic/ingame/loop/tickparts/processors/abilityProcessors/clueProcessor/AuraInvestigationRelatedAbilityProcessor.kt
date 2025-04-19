package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.clueProcessor

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameAuraClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.springframework.stereotype.Component

@Component
class AuraInvestigationRelatedAbilityProcessor(
    private val inGameAuraClueRepository: InGameAuraClueRepository
) : ActiveAbilityProcessor {

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.ability == Ability.SEARCH_FOR_AURA
    }

    override fun processActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {
        val aura = globalGameData.clues.aura.first { it.stringId() == castAbility.targetId }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            aura.castedAbilityUsers += user.inGameId()
            inGameAuraClueRepository.save(aura)
        }
    }

    override fun finishActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData) {
        val aura = globalGameData.clues.aura.first { it.stringId() == castAbility.targetId }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            aura.castedAbilityUsers -= user.inGameId()
            inGameAuraClueRepository.save(aura)
        }
    }
}