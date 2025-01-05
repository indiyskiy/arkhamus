package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.searchclue.v2

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.clues.RedisSoundClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.SoundClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.redis.parts.RedisSoundClueJammer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForSoundAbilityCast(
    private val timeEventHandler: TimeEventHandler,
    private val redisSoundClueRepository: RedisSoundClueRepository
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
        sourceUser: RedisGameUser,
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
        user: RedisGameUser,
        target: WithTrueIngameId,
        data: GlobalGameData
    ) {
        with(target as RedisSoundClueJammer) {

            this.turnedOn = false
            val soundClue = data.clues.sound.first { it.id == this.id }
            soundClue.soundClueJammers.first { it.inGameId == this.inGameId }.turnedOn = false
            redisSoundClueRepository.save(soundClue)

            timeEventHandler.createEvent(
                game = data.game,
                eventType = RedisTimeEventType.SOUND_CLUE_JAMMER_TURN_OFF,
                sourceObject = user,
                targetObject = target,
                location = Location(this.x, this.y, this.z),
                timeLeft = RedisTimeEventType.SOUND_CLUE_JAMMER_TURN_OFF.getDefaultTime()
            )
        }
    }
}