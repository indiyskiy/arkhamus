package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.clueProcessor

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.InscriptionClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameInscriptionClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InscriptionInvestigationRelatedAbilityProcessor(
    private val inGameInscriptionClueRepository: InGameInscriptionClueRepository,
    private val inscriptionClueHandler: InscriptionClueHandler
) : ActiveAbilityProcessor {

    companion object {
        private val logger = LoggingUtils.getLogger<InscriptionInvestigationRelatedAbilityProcessor>()
    }

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.ability == Ability.SEARCH_FOR_INSCRIPTION
    }

    override fun processActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData) {
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
        } ?: logger.error(
            "user {} or inscription {} is null",
            user?.inGameId() ?: "null",
            inscription?.stringId() ?: "null"
        )
    }
}