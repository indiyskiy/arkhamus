package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.VoteSpotRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.VoteSpot
import com.arkhamusserver.arkhamus.view.levelDesign.VoteSpotFromJson
import org.postgresql.geometric.PGpoint
import org.springframework.stereotype.Component

@Component
class LevelDesignVoteSpotInfoProcessor(
    private val voteSpotRepository: VoteSpotRepository,
) {
    fun processVoteSpots(voteSpots: List<VoteSpotFromJson>, savedLevel: Level) {
        voteSpots.forEach { voteSpot ->
            VoteSpot(
                point = PGpoint(voteSpot.x!!, voteSpot.y!!),
                inGameId = voteSpot.id!!,
                interactionRadius = voteSpot.interactionRadius!!,
                level = savedLevel
            ).apply {
                voteSpotRepository.save(this)
            }
        }
    }

}