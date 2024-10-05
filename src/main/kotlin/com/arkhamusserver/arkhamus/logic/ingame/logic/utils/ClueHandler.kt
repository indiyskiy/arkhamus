package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GameDataLevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.logic.ingame.toGod
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ClueHandler(
    private val redisClueRepository: RedisClueRepository
) {

    companion object {
        private val random: Random = Random(System.currentTimeMillis())
    }

    fun filterClues(
        clues: List<RedisClue>,
        inZones: List<LevelZone>,
        user: RedisGameUser
    ): List<RedisClue> {
        val possibleClues = Clue.values().filter {
            (it.visibilityModifiers intersect user.visibilityModifiers).isNotEmpty()
        }
        val zonesSet = inZones.filter {
            it.zoneType == ZoneType.CLUE
        }.map {
            it.zoneId
        }.toSet()
        return clues.filter { it.clue in possibleClues && it.levelZoneId in zonesSet }
    }

    fun addClues(
        game: GameSession,
        clueZones: List<RedisLevelZone>,
        clue: Clue,
        number: Int
    ) {
        val zoneIds = clueZones.shuffled(random).take(number).map { it.inGameId() }
        zoneIds.forEach {
            addClueToZone(game, it, clue)
        }
    }

    private fun addClueToZone(
        game: GameSession,
        zoneId: Long,
        clue: Clue
    ) {
        val gameId = game.id!!
        addClueToZone(gameId, zoneId, clue)
    }

    private fun addClueToZone(
        gameId: Long,
        zoneId: Long,
        clue: Clue
    ) {
        val redisClue = RedisClue(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = gameId,
            levelZoneId = zoneId,
            clue = clue,
            visibilityModifiers = clue.visibilityModifiers.map { it.name }.toMutableSet()
        )
        redisClueRepository.save(redisClue)
    }

    fun addRandomClue(data: GlobalGameData) {
        val existingClues = data.clues
        val clueZones = data.levelGeometryData.zones.filter { it.zoneType == ZoneType.CLUE }
        val clueTypes = data.game.godId.toGod()?.getTypes()
        clueTypes?.let { clueTypesNotNull ->
            clueTypesNotNull.map { clueType ->
                clueZones.mapNotNull { zone ->
                    if (clueExistAlready(zone, clueType, existingClues)) {
                        null
                    } else {
                        zone to clueType
                    }
                }
            }.flatten().randomOrNull()?.let { (zone, clueType) ->
                addClueToZone(
                    data.game.gameId!!,
                    zone.zoneId,
                    clueType
                )
            }
        }
    }

    fun removeRandomClue(data: GlobalGameData) {
        val existingClues = data.clues
        existingClues.randomOrNull()?.let { clue ->
            redisClueRepository.delete(clue)
        }
    }

    private fun clueExistAlready(
        zone: GameDataLevelZone,
        type: Clue,
        clues: List<RedisClue>
    ): Boolean {
        return clues.any { it.clue == type && it.levelZoneId == zone.zoneId }
    }
}