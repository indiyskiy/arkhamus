package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.VoteSpotRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.VoteSpot
import com.arkhamusserver.arkhamus.view.levelDesign.VoteSpotFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignVoteSpotInfoProcessor(
    private val voteSpotRepository: VoteSpotRepository,
) {
    fun processVoteSpots(voteSpots: List<VoteSpotFromJson>, savedLevel: Level) {
        voteSpots.forEach { voteSpot ->
            VoteSpot(
                x = voteSpot.x!!,
                y = voteSpot.y!!,
                z = voteSpot.z!!,
                inGameId = voteSpot.id!!,
                interactionRadius = voteSpot.interactionRadius!!,
                level = savedLevel,
                zoneId = voteSpot.zoneId!!
            ).apply {
                voteSpotRepository.save(this)
            }
        }
    }

}