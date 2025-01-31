package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameUserVoteSpotRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameVoteSpotRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.VoteSpotRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.VoteSpot
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameUserVoteSpot
import com.arkhamusserver.arkhamus.model.ingame.InGameVoteSpot
import org.springframework.stereotype.Component

@Component
class GameStartVoteSpotLogic(
    private val voteSpotRepository: VoteSpotRepository,
    private val inGameVoteSpotRepository: InGameVoteSpotRepository,
    private val inGameUserVoteSpotRepository: InGameUserVoteSpotRepository
) {

    companion object {
        private val DEFAULT_ITEM = Item.VOTE_TOKEN
    }

    fun createVoteSpots(levelId: Long, game: GameSession, users: List<InGameGameUser>) {
        val voteSpots = voteSpotRepository.findByLevelId(levelId)
        voteSpots.forEach { voteSpot ->
            val inGameVoteSpot = createVoteSpot(voteSpot, game, users)
            users.forEach { user ->
                createUserVoteSpot(inGameVoteSpot, game, user)
            }
        }
    }

    private fun createUserVoteSpot(
        voteSpot: InGameVoteSpot,
        game: GameSession,
        user: InGameGameUser
    ) {
        inGameUserVoteSpotRepository.save(
            InGameUserVoteSpot(
                id = generateRandomId(),
                gameId = game.id!!,
                voteSpotId = voteSpot.voteSpotId,
                userId = user.inGameId(),
                votesForUserIds = mutableListOf()
            )
        )
    }

    private fun createVoteSpot(
        voteSpot: VoteSpot,
        game: GameSession,
        allUsers: List<InGameGameUser>
    ) =
        inGameVoteSpotRepository.save(
            InGameVoteSpot(
                id = generateRandomId(),
                gameId = game.id!!,
                voteSpotId = voteSpot.inGameId,
                x = voteSpot.x,
                y = voteSpot.y,
                z = voteSpot.z,
                zoneId = voteSpot.zoneId,
                interactionRadius = voteSpot.interactionRadius,
                costValue = 1,
                costItem = DEFAULT_ITEM,
                bannedUsers = mutableListOf(),
                availableUsers = allUsers.map { it.inGameId() }.toMutableList(),
                visibilityModifiers = setOf(VisibilityModifier.ALL)
            )
        )

}