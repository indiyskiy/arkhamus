package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelZoneRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartClueLogic(
    private val redisClueRepository: RedisClueRepository,
    private val zoneRepository: LevelZoneRepository
) {
    private val random: Random = Random(System.currentTimeMillis())

    fun createClues(
        levelId: Long,
        game: GameSession
    ) {
        val zones = zoneRepository.findByLevelId(levelId)
        val allClues = game.god?.let {
            it.getTypes().map { clue ->
                RedisClue(
                    id = Generators.timeBasedEpochGenerator().generate().toString(),
                    gameId = game.id!!,
                    levelZoneId = zones.random(random).inGameId,
                    clue = clue
                )
            }
        } ?: emptyList()
        redisClueRepository.saveAll(allClues)
    }

}