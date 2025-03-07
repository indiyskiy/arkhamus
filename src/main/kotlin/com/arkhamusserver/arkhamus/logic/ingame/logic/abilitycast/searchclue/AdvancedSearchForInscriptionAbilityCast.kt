package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.InscriptionClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameInscriptionClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
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
class AdvancedSearchForInscriptionAbilityCast(
    private val timeEventHandler: TimeEventHandler,
    private val inGameInscriptionClueRepository: InGameInscriptionClueRepository,
    private val inscriptionClueHandler: InscriptionClueHandler
) : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(AdvancedSearchForInscriptionAbilityCast::class.java)
        val DEFAULT_NULLIFY_TIME = MINUTE_IN_MILLIS
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SEARCH_FOR_INSCRIPTION
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        logger.info("cast $ability")
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        val targetWithGameTags = abilityRequestProcessData.target as? WithTrueIngameId
        if (targetWithGameTags == null) return false
        castAbility(user, targetWithGameTags, globalGameData)
        return true
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
        castAbility(user, targetWithGameTags, globalGameData)
        return true
    }

    private fun castAbility(
        user: InGameUser,
        target: WithTrueIngameId,
        data: GlobalGameData
    ) {
        with(target as InGameInscriptionClueGlyph) {
            val inscriptionClue = data.clues.inscription.firstOrNull {
                it.inscriptionClueGlyphs.any {
                    it.inGameId() == this.inGameId()
                }
            }
            inscriptionClue?.let { inscriptionClueNotNull ->
                val targetedGlyph = inscriptionClueNotNull.inscriptionClueGlyphs.first {
                    it.inGameId() == this.inGameId()
                }
                val rightGlyph = targetedGlyph.value == this.value
                if (rightGlyph) {
                    makeClueVisible(
                        inscriptionClue,
                        user,
                        data
                    )
                } else {
                    resetGlyphs(
                        inscriptionClueNotNull,
                        user,
                    )
                }
            }
        }
    }

    private fun makeClueVisible(
        targetedClue: InGameInscriptionClue,
        user: InGameUser,
        data: GlobalGameData
    ) {
        targetedClue.castedAbilityUsers += user.inGameId()
        targetedClue.inscriptionClueGlyphs.forEach {
            it.value = targetedClue.value
        }
        inGameInscriptionClueRepository.save(targetedClue)

        timeEventHandler.createEvent(
            game = data.game,
            eventType = InGameTimeEventType.INSCRIPTION_CLUE_VISIBLE,
            sourceObject = user,
            targetObject = targetedClue,
            location = Location(targetedClue.x, targetedClue.y, targetedClue.z),
            timeLeft = InGameTimeEventType.INSCRIPTION_CLUE_VISIBLE.getDefaultTime()
        )
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