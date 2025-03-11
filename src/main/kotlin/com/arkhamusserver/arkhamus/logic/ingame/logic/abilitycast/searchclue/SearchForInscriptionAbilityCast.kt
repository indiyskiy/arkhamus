package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.InscriptionClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameInscriptionClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameInscriptionClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameInscriptionClueGlyph
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SearchForInscriptionAbilityCast(
    private val inGameInscriptionClueRepository: InGameInscriptionClueRepository,
    private val inscriptionClueHandler: InscriptionClueHandler
) : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(SearchForInscriptionAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SEARCH_FOR_INSCRIPTION
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        val targetWithInGameId = abilityRequestProcessData.target as? WithTrueIngameId
        if (targetWithInGameId == null) return false
        logger.info("cast $ability on ${targetWithInGameId.inGameId()}")
        return castAbility(user, targetWithInGameId, globalGameData)
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        val user = sourceUser
        val targetWithGameTags = target as? WithTrueIngameId
        if (targetWithGameTags == null) return false
        return castAbility(user, targetWithGameTags, globalGameData)
    }

    private fun castAbility(
        user: InGameUser,
        target: WithTrueIngameId,
        data: GlobalGameData
    ): Boolean {
        with(target as InGameInscriptionClueGlyph) {
            val inscriptionClue = data.clues.inscription.firstOrNull {
                it.inscriptionClueGlyphs.any {
                    it.inGameId() == this.inGameId()
                }
            }
            if (inscriptionClue == null) return false
            logger.info("found related clue: ${inscriptionClue.inGameId()}")
            val rightGlyph = inscriptionClue.value == this.value
            if (rightGlyph) {
                makeClueVisible(
                    inscriptionClue,
                    user
                )
            } else {
                resetGlyphs(
                    inscriptionClue,
                    user,
                )
            }
            return true
        }
    }

    private fun makeClueVisible(
        targetedClue: InGameInscriptionClue,
        user: InGameUser,
    ) {
        targetedClue.castedAbilityUsers += user.inGameId()
        inGameInscriptionClueRepository.save(targetedClue)
    }

    private fun resetGlyphs(
        targetedClue: InGameInscriptionClue,
        user: InGameUser,
    ) {
        targetedClue.castedAbilityUsers -= user.inGameId()
        inscriptionClueHandler.shuffleGlyphValues(targetedClue)
        inGameInscriptionClueRepository.save(targetedClue)
    }

}