package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.CrafterRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Crafter
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class GameStartCrafterLogic(
    private val redisCrafterRepository: RedisCrafterRepository,
    private val crafterRepository: CrafterRepository,
) {
    fun createCrafters(
        levelId: Long,
        game: GameSession
    ) {
        val allLevelCrafters = crafterRepository.findByLevelId(levelId)
        allLevelCrafters.forEach { dbCrafter ->
            with(createCrafter(game, dbCrafter)) {
                redisCrafterRepository.save(this)
            }
        }
    }

    private fun createCrafter(
        game: GameSession,
        dbCrafter: Crafter,
    ) = RedisCrafter(
        id = Generators.timeBasedEpochGenerator().generate().toString(),
        crafterId = dbCrafter.inGameId,
        gameId = game.id!!,
        crafterType = dbCrafter.crafterType
    ).apply {
        this.x = dbCrafter.point.x
        this.y = dbCrafter.point.y
        this.interactionRadius = dbCrafter.interactionRadius
        this.items = emptyMap<Int, Int>().toMutableMap()
    }
}