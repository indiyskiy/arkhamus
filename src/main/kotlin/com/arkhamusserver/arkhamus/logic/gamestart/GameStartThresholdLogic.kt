package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameThresholdRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.ThresholdRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Threshold
import com.arkhamusserver.arkhamus.model.ingame.InGameThreshold
import org.springframework.stereotype.Component

@Component
class GameStartThresholdLogic(
    private val thresholdRepository: ThresholdRepository,
    private val inGameThresholdRepository: InGameThresholdRepository,
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
        inGameThresholdRepository.save(
            InGameThreshold(
                id = generateRandomId(),
                gameId = game.id!!,
                thresholdId = threshold.inGameId,
                x = threshold.x,
                y = threshold.y,
                z = threshold.z,
                zoneId = threshold.zoneId,
                type = threshold.type
            )
        )

}