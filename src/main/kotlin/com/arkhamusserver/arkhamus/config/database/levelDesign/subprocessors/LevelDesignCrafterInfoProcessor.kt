package com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.CrafterRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Crafter
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.view.levelDesign.CrafterFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignCrafterInfoProcessor(
    private val crafterRepository: CrafterRepository,
) {

    fun processCrafters(
        containers: List<CrafterFromJson>,
        savedLevel: Level?,
    ) {
        containers.forEach { crafter ->
            Crafter(
                inGameId = crafter.id!!,
                interactionRadius = crafter.interactionRadius!!,
                x = crafter.x!!,
                y = crafter.y!!,
                z = crafter.z!!,
                level = savedLevel!!,
                crafterType = crafter.crafterType!!,
            ).apply {
                crafterRepository.save(this)
            }
        }
    }

}