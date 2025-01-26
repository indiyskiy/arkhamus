package com.arkhamusserver.arkhamus.logic.ingame.logic.visibility

import com.arkhamusserver.arkhamus.model.database.entity.game.VisibilityDoor
import com.arkhamusserver.arkhamus.model.database.entity.game.VisibilityWall
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level

data class ObstaclesMap(
    val xMin: Double,
    val zMin: Double,
    val xMax: Double,
    val zMax: Double,
    val obstacles: List<VisibilityObstacle>
) {
    companion object {
        fun build(
            doors: List<VisibilityDoor>,
            walls: List<VisibilityWall>,
            levelData: Level
        ): ObstaclesMap {
            return ObstaclesMap(
                0.0,
                0.0,
                levelData.levelWidth.toDouble(),
                levelData.levelHeight.toDouble(),
                doors.map { VisibilityObstacle.fromDoor(it) } + walls.map{ VisibilityObstacle.fromWall(it) }
            )
        }
    }
}

