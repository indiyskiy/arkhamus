package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisDoorRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.DoorRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Door
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisDoor
import org.springframework.stereotype.Component

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
                id = generateRandomId(),
                gameId = game.id!!,
                doorId = door.inGameId,
                x = door.x,
                y = door.y,
                z = door.z,
                zoneId = door.zoneId,
                visibilityModifiers = setOf(VisibilityModifier.ALL)
            )
        )

}