package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.CrafterRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Crafter
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.view.levelDesign.CrafterFromJson
import org.postgresql.geometric.PGpoint
import org.springframework.stereotype.Component
import kotlin.collections.forEach

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
                point = PGpoint(crafter.x!!, crafter.y!!),
                level = savedLevel!!,
                crafterType = crafter.crafterType!!,
            ).apply {
                crafterRepository.save(this)
            }
        }
    }

}