package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.AltarRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Altar
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.view.levelDesign.AltarFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignAltarInfoProcessor(
    private val altarRepository: AltarRepository,
) {

    fun processAltars(
        altars: List<AltarFromJson>,
        savedLevel: Level?
    ) {
        altars.forEach { altar ->
            Altar(
                inGameId = altar.id,
                interactionRadius = altar.interactionRadius,
                x = altar.x!!,
                y = altar.y!!,
                z = altar.z!!,
                level = savedLevel
            ).apply {
                altarRepository.save(this)
            }
        }
    }

}