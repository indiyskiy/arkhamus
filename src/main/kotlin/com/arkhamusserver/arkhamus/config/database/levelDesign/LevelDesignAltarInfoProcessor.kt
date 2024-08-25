package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.AltarRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Altar
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.view.levelDesign.AltarFromJson
import org.postgresql.geometric.PGpoint
import org.springframework.stereotype.Component
import kotlin.collections.forEach

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
                point = PGpoint(altar.x!!, altar.y!!),
                level = savedLevel
            ).apply {
                altarRepository.save(this)
            }
        }
    }

}