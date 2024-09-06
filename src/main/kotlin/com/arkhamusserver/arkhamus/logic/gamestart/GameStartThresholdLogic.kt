package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisThresholdRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.ThresholdRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Threshold
import com.arkhamusserver.arkhamus.model.redis.RedisThreshold
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import kotlin.Long

@Component
class GameStartThresholdLogic(
    private val thresholdRepository: ThresholdRepository,
    private val redisThresholdRepository: RedisThresholdRepository,
) {

    fun createThresholds(levelId: Long, game: GameSession) {
        val thresholds = thresholdRepository.findByLevelId(levelId)
        thresholds.forEach { threshold ->
            createThreshold(threshold, game)
        }
    }

    private fun createThreshold(
        threshold: Threshold,
        game: GameSession,
    ) =
        redisThresholdRepository.save(
            RedisThreshold(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = game.id!!,
                thresholdId = threshold.inGameId,
                x = threshold.point.x,
                y = threshold.point.y,
                zoneId = threshold.zoneId,
            )
        )

}