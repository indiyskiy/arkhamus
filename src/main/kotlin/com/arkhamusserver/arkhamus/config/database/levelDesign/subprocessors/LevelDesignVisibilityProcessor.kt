package com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.VisibilityDoorRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.VisibilityWallRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.VisibilityDoor
import com.arkhamusserver.arkhamus.model.database.entity.game.VisibilityWall
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.view.levelDesign.LevelFromJson
import com.arkhamusserver.arkhamus.view.levelDesign.PointJson
import com.arkhamusserver.arkhamus.view.levelDesign.VisibilityDoorFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignVisibilityProcessor(
    private val visibilityDoorRepository: VisibilityDoorRepository,
    private val visibilityWallRepository: VisibilityWallRepository
) {

    fun processVisibilityObjects(levelFromJson: LevelFromJson, savedLevel: Level) {
        processVisibilityWalls(levelFromJson.visibilityWalls, savedLevel)
        processVisibilityDoors(levelFromJson.visibilityDoors, savedLevel)
    }

    private fun processVisibilityDoors(doors: List<VisibilityDoorFromJson>, savedLevel: Level) {
        doors.forEach{ door ->
            val point1 = door.points[0]
            val point2 = door.points[1]
            VisibilityDoor(
                x1 = point1.x!!,
                y1 = point1.y!!,
                z1 = point1.z!!,
                x2 = point2.x!!,
                y2 = point2.y!!,
                z2 = point2.z!!,
                inGameId = door.id,
                level = savedLevel
            ).apply {
                visibilityDoorRepository.save(this)
            }
        }
    }

    private fun processVisibilityWalls(walls: List<List<PointJson>>, savedLevel: Level) {
        walls.forEach { wall ->
                val parsedWalls = wall.drop(1).fold(emptyList<VisibilityWall>() to wall.first()) { acc, point ->
                    (acc.first + VisibilityWall(
                        // TODO get rid of all the screaming
                        x1 = acc.second.x!!,
                        y1 = acc.second.y!!,
                        z1 = acc.second.z!!,
                        x2 = point.x!!,
                        y2 = point.y!!,
                        z2 = point.z!!,
                        level = savedLevel
                    )) to point
                }
            parsedWalls.first.forEach { parsedWall ->
                visibilityWallRepository.save(parsedWall)
            }
        }
    }
}