package com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelTaskRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelTask
import com.arkhamusserver.arkhamus.view.levelDesign.LevelTaskFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignLevelTaskInfoProcessor(
    private val levelTaskRepository: LevelTaskRepository,
) {
    fun processLevelTasksFromJson(levelTasks: List<LevelTaskFromJson>, savedLevel: Level): List<LevelTask> {
        levelTasks.map { jsonLevelTask ->
            LevelTask(
                inGameId = jsonLevelTask.id!!,
                x = jsonLevelTask.x!!,
                y = jsonLevelTask.y!!,
                z = jsonLevelTask.z!!,
                interactionRadius = jsonLevelTask.interactionRadius!!,
                level = savedLevel,
                name = jsonLevelTask.name!!
            )
        }.apply {
            return levelTaskRepository.saveAll(this).toList()
        }
    }
}