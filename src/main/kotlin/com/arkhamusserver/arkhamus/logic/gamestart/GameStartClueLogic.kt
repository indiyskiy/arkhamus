package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class GameStartClueLogic(
    private val redisClueRepository: RedisClueRepository,
) {
    private val random: Random = Random(System.currentTimeMillis())

    @Transactional
    fun createClues(
        game: GameSession,
        zones: List<RedisLevelZone>
    ) {
        val clueZones = zones.filter { it.zoneType == ZoneType.CLUE }
        val allClues = game.god?.let {
            it.getTypes().map { clue ->
                RedisClue(
                    id = Generators.timeBasedEpochGenerator().generate().toString(),
                    gameId = game.id!!,
                    levelZoneId = clueZones.random(random).levelZoneId,
                    clue = clue
                )
            }
        } ?: emptyList()
        redisClueRepository.saveAll(allClues)
    }

}