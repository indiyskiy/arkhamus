package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.clues.RedisSoundClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.SoundClueJammerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.SoundClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import com.arkhamusserver.arkhamus.model.redis.clues.RedisSoundClue
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.redis.parts.RedisSoundClueJammer
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.SoundClueJammerResponse
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class SoundClueHandler(
    private val soundClueRepository: SoundClueRepository,
    private val soundClueJammerRepository: SoundClueJammerRepository,
    private val redisSoundClueRepository: RedisSoundClueRepository,
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler,
    private val zonesHandler: ZonesHandler
) : AdvancedClueHandler {

    companion object {
        const val MAX_ON_GAME = 7
        const val JAMMERS = 7
        private val random: Random = Random(System.currentTimeMillis())
    }

    override fun accept(clues: List<Clue>): Boolean {
        return clues.contains(Clue.SOUND)
    }

    override fun accept(clue: Clue): Boolean {
        return clue == Clue.SOUND
    }

    override fun accept(target: WithStringId): Boolean {
        return target is RedisSoundClue
    }

    override fun canBeAdded(container: CluesContainer): Boolean {
        return container.sound.any { !it.turnedOn }
    }

    override fun addClue(
        data: GlobalGameData
    ) {
        data.clues.sound.filter { !it.turnedOn }.random(random).apply {
            turnedOn = true
            redisSoundClueRepository.save(this)
        }
    }

    override fun canBeRemoved(container: CluesContainer): Boolean {
        return container.sound.any { it.turnedOn }
    }

    override fun canBeRemoved(
        user: RedisGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        val sound = target as RedisSoundClue
        return userLocationHandler.userCanSeeTargetInRange(
            whoLooks = user,
            target = sound,
            levelGeometryData = data.levelGeometryData,
            range = Ability.CLEAN_UP_CLUE.range!!.toDouble(),
            affectedByBlind = false,
            heightAffectVision = false,
            geometryAffectsVision = true,
        )
    }

    override fun anyCanBeRemoved(
        user: RedisGameUser,
        data: GlobalGameData
    ): Boolean {
        return data.clues.sound.any {
            canBeRemoved(user, it, data)
        }
    }

    override fun removeRandom(container: CluesContainer) {
        val soundClue = container.sound.filter { it.turnedOn }.randomOrNull()
        soundClue?.let {
            it.turnedOn = false
            redisSoundClueRepository.save(it)
        }
    }

    override fun removeTarget(
        target: WithStringId,
        container: CluesContainer
    ) {
        val soundClue = container.sound.find { it.inGameId() == target.stringId().toLong() } ?: return
        soundClue.turnedOn = false
        redisSoundClueRepository.save(soundClue)
    }

    override fun addClues(
        session: GameSession,
        god: God,
        zones: List<RedisLevelZone>,
        activeCluesOnStart: Int
    ) {
        val soundClues = soundClueRepository.findByLevelId(session.gameSessionSettings.level!!.id!!)
        val soundClueJammers = soundClueJammerRepository.findByLevelId(session.gameSessionSettings.level!!.id!!)
        val soundCluesForGameSession = soundClues.shuffled(random).take(MAX_ON_GAME)
        val redisSoundClues = soundCluesForGameSession.map {
            val clue = RedisSoundClue(
                id = generateRandomId(),
                gameId = session.id!!,
                redisSoundId = it.inGameId,
                x = it.x,
                y = it.y,
                z = it.z,
                visibilityModifiers = setOf(
                    VisibilityModifier.HAVE_ITEM_SOUND,
                ),
                turnedOn = false,
                zoneId = it.zone!!.inGameId,
                soundClueJammers = soundClueJammers.filter { jammer ->
                    it.inGameId == jammer.relatedClue!!.inGameId
                }.map { jammer ->
                    RedisSoundClueJammer(
                        id = generateRandomId(),
                        gameId = session.id!!,
                        x = jammer.x(),
                        y = jammer.y(),
                        z = jammer.z(),
                        inGameId = jammer.inGameId,
                        visibilityModifiers = setOf(VisibilityModifier.HAVE_ITEM_SOUND),
                        soundClueId = it.inGameId,
                        zoneId = it.zone!!.inGameId,
                        interactionRadius = jammer.interactionRadius,
                        turnedOn = false
                    )
                }
            )
            clue.soundClueJammers
                .shuffled(random)
                .take(JAMMERS).forEach {
                    it.turnedOn = true
                }
            clue
        }
        if (god.getTypes().contains(Clue.SOUND)) {
            val turnedOn = redisSoundClues.shuffled(random).take(activeCluesOnStart)
            turnedOn.forEach {
                it.turnedOn = true
            }
        }
        redisSoundClueRepository.saveAll(redisSoundClues)
    }

    override fun mapActualClues(
        container: CluesContainer,
        user: RedisGameUser,
        levelGeometryData: LevelGeometryData,
    ): List<ExtendedClueResponse> {
        return container.sound.filter {
            it.turnedOn == true
        }.filter {
            zonesHandler.inSameZone(
                user,
                it,
                levelGeometryData,
                types = setOf(ZoneType.SOUND)
            )
        }.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, Clue.SOUND)
        }.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.SOUND,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.SOUND_CLUE,
                x = null,
                y = null,
                z = null,
                possibleRadius = 0.0,
                additionalData = fillActualAdditionalData(it, user, levelGeometryData),
                turnedOn = true
            )
        }
    }

    override fun mapPossibleClues(
        container: CluesContainer,
        user: RedisGameUser,
        levelGeometryData: LevelGeometryData,
    ): List<ExtendedClueResponse> {
        val soundOptions = container.sound
        val filteredByVisibilityTags = soundOptions.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, it)
        }
        return filteredByVisibilityTags.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.SOUND,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.SOUND_CLUE,
                x = null,
                y = null,
                z = null,
                possibleRadius = 0.0,
                additionalData = fillPossibleAdditionalData(it, user, levelGeometryData),
                turnedOn = it.turnedOn &&
                        it.soundClueJammers.all { !it.turnedOn } &&
                        zonesHandler.inSameZone(
                            user,
                            it,
                            levelGeometryData,
                            types = setOf(ZoneType.SOUND)
                        )
            )
        }
    }

    private fun fillPossibleAdditionalData(
        clue: RedisSoundClue,
        user: RedisGameUser,
        data: LevelGeometryData
    ): List<SoundClueJammerResponse> {
        return clue.soundClueJammers.filter {
            it.turnedOn
        }
            .filter {
                visibilityByTagsHandler.userCanSeeTarget(user, it)
            }.filter {
                zonesHandler.inSameZone(
                    user,
                    it,
                    data,
                    types = setOf(ZoneType.SOUND)
                )
            }.map {
                SoundClueJammerResponse(
                    id = it.inGameId(),
                    x = it.x,
                    y = it.y,
                    z = it.z,
                    turnedOn = it.turnedOn
                )
            }
    }

    private fun fillActualAdditionalData(
        clue: RedisSoundClue,
        user: RedisGameUser,
        data: LevelGeometryData
    ): List<SoundClueJammerResponse> {
        return clue.soundClueJammers.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, it)
        }.filter {
            zonesHandler.inSameZone(
                user,
                it,
                data,
                types = setOf(ZoneType.SOUND)
            )
        }.map {
            SoundClueJammerResponse(
                id = it.inGameId(),
                x = it.x,
                y = it.y,
                z = it.z,
                turnedOn = it.turnedOn
            )
        }
    }

}