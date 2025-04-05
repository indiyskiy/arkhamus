package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameSoundClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.SoundClueJammerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.SoundClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.ClueState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZone
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameSoundClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameSoundClueJammer
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.SoundClueAdditionalDataResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.SoundClueJammerResponse
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class SoundClueHandler(
    private val soundClueRepository: SoundClueRepository,
    private val soundClueJammerRepository: SoundClueJammerRepository,
    private val inGameSoundClueRepository: InGameSoundClueRepository,
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
        return target is InGameSoundClue
    }

    override fun canBeAdded(container: CluesContainer): Boolean {
        return container.sound.any { !it.turnedOn }
    }

    override fun addClue(
        data: GlobalGameData
    ) {
        data.clues.sound.filter { !it.turnedOn }.random(random).apply {
            turnedOn = true
            inGameSoundClueRepository.save(this)
        }
    }

    override fun canBeRemovedRandomly(container: CluesContainer): Boolean {
        return container.sound.any { it.turnedOn }
    }

    override fun canBeRemovedByAbility(
        user: InGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        val sound = target as InGameSoundClue
        return sound.turnedOn && userLocationHandler.userCanSeeTargetInRange(
            whoLooks = user,
            target = sound,
            levelGeometryData = data.levelGeometryData,
            range = Ability.CLEAN_UP_CLUE.range!!.toDouble(),
            affectedByBlind = false,
            heightAffectVision = false,
            geometryAffectsVision = true,
        )
    }

    override fun anyCanBeRemovedByAbility(
        user: InGameUser,
        data: GlobalGameData
    ): Boolean {
        return data.clues.sound.any {
            canBeRemovedByAbility(user, it, data)
        }
    }

    override fun removeRandom(container: CluesContainer) {
        val soundClue = container.sound.filter { it.turnedOn }.randomOrNull()
        soundClue?.let {
            it.turnedOn = false
            inGameSoundClueRepository.save(it)
        }
    }

    override fun removeTarget(
        target: WithStringId,
        data: GlobalGameData
    ) {
        val soundClue = data.clues.sound.find { it.inGameId() == target.stringId().toLong() } ?: return
        soundClue.turnedOn = false
        inGameSoundClueRepository.save(soundClue)
    }

    override fun addClues(
        session: GameSession,
        god: God,
        zones: List<InGameLevelZone>,
        activeCluesOnStart: Int
    ) {
        val soundClues = soundClueRepository.findByLevelId(session.gameSessionSettings.level!!.id!!)
        val soundClueJammers = soundClueJammerRepository.findByLevelId(session.gameSessionSettings.level!!.id!!)
        val soundCluesForGameSession = soundClues.shuffled(random).take(MAX_ON_GAME)
        val inGameSoundClues = soundCluesForGameSession.map {
            val clue = InGameSoundClue(
                id = generateRandomId(),
                gameId = session.id!!,
                inGameSoundId = it.inGameId,
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
                    InGameSoundClueJammer(
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
            val turnedOn = inGameSoundClues.shuffled(random).take(activeCluesOnStart)
            turnedOn.forEach {
                it.turnedOn = true
            }
        }
        inGameSoundClueRepository.saveAll(inGameSoundClues)
    }

    override fun mapActualClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return container.sound.filter {
            it.turnedOn == true
        }.filter {
            zonesHandler.inSameZone(
                user,
                it,
                data.levelGeometryData,
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
                additionalData = fillActualAdditionalData(it, user, data.levelGeometryData),
                state = ClueState.ACTIVE_CLUE
            )
        }
    }

    override fun mapPossibleClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
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
                additionalData = fillPossibleAdditionalData(it, user, data.levelGeometryData),
                state = countState(it, user, data.levelGeometryData),
            )
        }
    }

    private fun countState(
        clue: InGameSoundClue,
        user: InGameUser,
        levelGeometryData: LevelGeometryData
    ): ClueState {
        val turnedOn = clue.turnedOn
        val jammersTurnedOff = clue.soundClueJammers.all { !it.turnedOn }
        val inSameZone = zonesHandler.inSameZone(
            user,
            clue,
            levelGeometryData,
            types = setOf(ZoneType.SOUND)
        )
        if (!jammersTurnedOff || !inSameZone) {
            return ClueState.ACTIVE_UNKNOWN
        }
        return if (turnedOn) ClueState.ACTIVE_CLUE else ClueState.ACTIVE_NO_CLUE
    }

    private fun fillPossibleAdditionalData(
        clue: InGameSoundClue,
        user: InGameUser,
        data: LevelGeometryData
    ): SoundClueAdditionalDataResponse {
        return defaultSoundClueJammersInfo(clue, user, data)
    }

    private fun fillActualAdditionalData(
        clue: InGameSoundClue,
        user: InGameUser,
        data: LevelGeometryData
    ): SoundClueAdditionalDataResponse {
        return defaultSoundClueJammersInfo(clue, user, data)
    }

    private fun defaultSoundClueJammersInfo(
        clue: InGameSoundClue,
        user: InGameUser,
        data: LevelGeometryData
    ): SoundClueAdditionalDataResponse {
        return SoundClueAdditionalDataResponse().apply {
            this.soundClueJammers = clue.soundClueJammers.filter {
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

}