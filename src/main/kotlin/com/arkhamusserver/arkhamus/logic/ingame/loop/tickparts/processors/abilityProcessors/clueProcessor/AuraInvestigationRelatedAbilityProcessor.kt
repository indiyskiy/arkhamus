package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.clueProcessor

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.PretendCultistAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameAuraClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AuraInvestigationRelatedAbilityProcessor(
    private val inGameAuraClueRepository: InGameAuraClueRepository
) : ActiveAbilityProcessor {

    companion object {
        private val logger = LoggingUtils.getLogger<AuraInvestigationRelatedAbilityProcessor>()
    }

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