package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.ContainerRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Container
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.view.levelDesign.ContainerFromJson
import org.postgresql.geometric.PGpoint
import org.springframework.stereotype.Component
import kotlin.collections.forEach

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
                point = PGpoint(container.x!!, container.y!!),
                level = savedLevel
            ).apply {
                containerRepository.save(this)
            }
        }
    }
}