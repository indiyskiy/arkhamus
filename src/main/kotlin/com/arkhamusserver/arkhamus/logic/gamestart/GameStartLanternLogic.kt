package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameLanternRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LanternRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Lantern
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameLantern
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class GameStartLanternLogic(
    private val inGameLanternRepository: InGameLanternRepository,
    private val lanternRepository: LanternRepository,
) {
    companion object {
        const val FILLED_ON_START: Int = 2
        private val random: Random = Random(System.currentTimeMillis())
    }

    @Transactional
    fun createLanterns(
        levelId: Long,
        game: GameSession
    ) {
        val allLevelLanterns = lanternRepository.findByLevelId(levelId)
        allLevelLanterns.shuffled(random).forEachIndexed { i, dbLantern ->
            with(createLantern(game, dbLantern, i)) {
                inGameLanternRepository.save(this)
            }
        }
    }

    private fun createLantern(
        game: GameSession,
        dbLantern: Lantern,
        number: Int
    ) = InGameLantern(
        id = generateRandomId(),
        lanternId = dbLantern.inGameId,
        gameId = game.id!!,
        x = dbLantern.x,
        y = dbLantern.y,
        z = dbLantern.z,
        lightRange = dbLantern.lightRange!!,
        interactionRadius = dbLantern.interactionRadius!!,
        fuel = if (number < FILLED_ON_START) {
            100.0
        } else {
            0.0
        },
        lanternState = if (number < FILLED_ON_START) {
            LanternState.FILLED
        } else {
            LanternState.EMPTY
        },
        visibilityModifiers = setOf(VisibilityModifier.ALL)
    )

}