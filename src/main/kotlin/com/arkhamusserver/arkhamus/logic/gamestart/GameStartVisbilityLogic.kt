package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.logic.visibility.ObstaclesMap
import com.arkhamusserver.arkhamus.logic.ingame.logic.visibility.VisibilityMap
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisVisibilityMapRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.VisibilityDoorRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.VisibilityWallRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.redis.RedisVisibilityMap
import org.springframework.stereotype.Component

@Component
class GameStartVisbilityLogic(
    private val levelRepository: LevelRepository,
    private val visibilityDoorRepository: VisibilityDoorRepository,
    private val visibilityWallRepository: VisibilityWallRepository,
    private val redisVisibilityMapRepository: RedisVisibilityMapRepository
) {

    fun createLevelData(levelId: Long, game: GameSession) {
        val doors = visibilityDoorRepository.findByLevelId(levelId)
        val walls = visibilityWallRepository.findByLevelId(levelId)
        val level = levelRepository.findByLevelId(levelId).first()
        val obstaclesMap = ObstaclesMap.build(doors, walls, level)
        val visibilityMap = VisibilityMap.build(
            obstaclesMap,
            VisibilityMap.MIN_SEGMENT_SIZE
        )
        redisVisibilityMapRepository.save(
            RedisVisibilityMap(
                id = generateRandomId(),
                gameId = game.id!!,
                visibilityMap = visibilityMap
            )
        )
    }
}