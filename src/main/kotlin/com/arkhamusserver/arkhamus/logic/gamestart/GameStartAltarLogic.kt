package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAltarRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.AltarRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.RitualAreaRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Altar
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameAltar
import com.arkhamusserver.arkhamus.model.ingame.InGameAltarHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameStartAltarLogic(
    private val inGameAltarRepository: InGameAltarRepository,
    private val inGameAltarHolderRepository: InGameAltarHolderRepository,
    private val altarRepository: AltarRepository,
    private val ritualAreaRepository: RitualAreaRepository,
) {

    @Transactional
    fun createAltars(
        levelId: Long,
        game: GameSession
    ) {
        val allLevelAltars = altarRepository.findByLevelId(levelId)
        allLevelAltars.forEach { dbAltar ->
            inGameAltarRepository.save(createAltar(game, dbAltar))
        }
        createAltarHolder(game, levelId)
    }

    private fun createAltarHolder(game: GameSession, levelId: Long) {
        val ritualArea = ritualAreaRepository.findByLevelId(levelId).first()

        inGameAltarHolderRepository.save(
            InGameAltarHolder(
                id = generateRandomId(),
                gameId = game.id!!,
                state = MapAltarState.OPEN,
                altarHolderId = ritualArea.inGameId,
                x = ritualArea.x,
                y = ritualArea.y,
                z = ritualArea.z,
                radius = ritualArea.radius
            )
        )
    }

    private fun createAltar(
        game: GameSession,
        dbAltar: Altar,
    ) = InGameAltar(
        id = generateRandomId(),
        altarId = dbAltar.inGameId!!,
        gameId = game.id!!,
        x = dbAltar.x,
        y = dbAltar.y,
        z = dbAltar.z,
        interactionRadius = dbAltar.interactionRadius!!,
        visibilityModifiers = setOf(VisibilityModifier.ALL)
    )

}