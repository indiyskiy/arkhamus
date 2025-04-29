package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.clueProcessor

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.InscriptionClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameInscriptionClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityActiveCast
import org.springframework.stereotype.Component

@Component
class InscriptionInvestigationRelatedAbilityProcessor(
    private val inGameInscriptionClueRepository: InGameInscriptionClueRepository,
    private val inscriptionClueHandler: InscriptionClueHandler
) : ActiveAbilityProcessor {

    override fun accepts(castAbility: InGameAbilityActiveCast): Boolean {
        return castAbility.ability == Ability.SEARCH_FOR_INSCRIPTION
    }

    override fun processActive(
        castAbility: InGameAbilityActiveCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: InGameAbilityActiveCast, globalGameData: GlobalGameData) {
        val inscription = globalGameData.clues.inscription.firstOrNull {
            it.inscriptionClueGlyphs.any {
                it.stringId() == castAbility.targetId
            }
        }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let { userNotNull ->
            inscription?.let { inscriptionNotNull ->
                inscriptionNotNull.castedAbilityUsers -= userNotNull.inGameId()
                if (inscriptionNotNull.castedAbilityUsers.isEmpty()) {
                    inscriptionClueHandler.shuffleGlyphValues(inscriptionNotNull)
                }
                inGameInscriptionClueRepository.save(inscriptionNotNull)
            }
        }
    }
}