package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameCrafterRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.CrafterRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Crafter
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameStartCrafterLogic(
    private val inGameCrafterRepository: InGameCrafterRepository,
    private val crafterRepository: CrafterRepository,
) {

    @Transactional
    fun createCrafters(
        levelId: Long,
        game: GameSession
    ) {
        val allLevelCrafters = crafterRepository.findByLevelId(levelId)
        allLevelCrafters.forEach { dbCrafter ->
            with(createCrafter(game, dbCrafter)) {
                inGameCrafterRepository.save(this)
            }
        }
    }

    private fun createCrafter(
        game: GameSession,
        dbCrafter: Crafter,
    ) = InGameCrafter(
        id = generateRandomId(),
        crafterId = dbCrafter.inGameId,
        gameId = game.id!!,
        crafterType = dbCrafter.crafterType,
        x = dbCrafter.x,
        y = dbCrafter.y,
        z = dbCrafter.z,
        gameTags = mutableSetOf(),
        visibilityModifiers = setOf(VisibilityModifier.ALL)
    ).apply {
        this.interactionRadius = dbCrafter.interactionRadius
        this.items = emptyList()
    }
}