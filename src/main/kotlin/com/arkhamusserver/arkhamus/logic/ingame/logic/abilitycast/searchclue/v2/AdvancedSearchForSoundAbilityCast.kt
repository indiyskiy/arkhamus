package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue.v2

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameSoundClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameSoundClueJammer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForSoundAbilityCast(
    private val timeEventHandler: TimeEventHandler,
    private val inGameSoundClueRepository: InGameSoundClueRepository
) : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(AdvancedSearchForSoundAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.ADVANCED_SEARCH_FOR_SOUND
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
        with(target as InGameSoundClueJammer) {
            this.turnedOn = false
            val soundClue = data.clues.sound.firstOrNull {
                it.soundClueJammers.any {
                    it.inGameId() == this.inGameId()
                }
            }
            soundClue?.let { soundClueNotNull ->
                soundClueNotNull.soundClueJammers.first {
                    it.inGameId() == this.inGameId()
                }.turnedOn = false
                inGameSoundClueRepository.save(soundClueNotNull)
            }


            timeEventHandler.createEvent(
                game = data.game,
                eventType = InGameTimeEventType.SOUND_CLUE_JAMMER_TURN_OFF,
                sourceObject = user,
                targetObject = target,
                location = Location(this.x, this.y, this.z),
                timeLeft = InGameTimeEventType.SOUND_CLUE_JAMMER_TURN_OFF.getDefaultTime()
            )
        }
    }
}