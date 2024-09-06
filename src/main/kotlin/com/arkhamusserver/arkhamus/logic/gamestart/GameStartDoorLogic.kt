package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisDoorRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.DoorRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Door
import com.arkhamusserver.arkhamus.model.redis.RedisDoor
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import kotlin.Long

@Component
class GameStartDoorLogic(
    private val doorRepository: DoorRepository,
    private val redisDoorRepository: RedisDoorRepository,
) {

    fun createDoors(levelId: Long, game: GameSession) {
        val doors = doorRepository.findByLevelId(levelId)
        doors.forEach { door ->
            createDoor(door, game)
        }
    }

    private fun createDoor(
        door: Door,
        game: GameSession,
    ) =
        redisDoorRepository.save(
            RedisDoor(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = game.id!!,
                doorId = door.inGameId,
                x = door.point.x,
                y = door.point.y,
                zoneId = door.zoneId,
            )
        )

}