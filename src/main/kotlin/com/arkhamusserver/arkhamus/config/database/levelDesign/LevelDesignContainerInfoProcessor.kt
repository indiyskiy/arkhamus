package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.ContainerRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Container
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.view.levelDesign.ContainerFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignContainerInfoProcessor(
    private val containerRepository: ContainerRepository,
) {
    fun processContainers(
        containers: List<ContainerFromJson>,
        savedLevel: Level?
    ) {
        containers.forEach { container ->
            Container(
                inGameId = container.id!!,
                interactionRadius = container.interactionRadius!!,
                containerTags = container.containerTags?.toSet() ?: emptySet(),
                x = container.x!!,
                y = container.y!!,
                z = container.z!!,
                level = savedLevel
            ).apply {
                containerRepository.save(this)
            }
        }
    }
}