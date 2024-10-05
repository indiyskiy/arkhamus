package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.AltarRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.RitualAreaRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Altar
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameStartAltarLogic(
    private val redisAltarRepository: RedisAltarRepository,
    private val redisAltarHolderRepository: RedisAltarHolderRepository,
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
            redisAltarRepository.save(createAltar(game, dbAltar))
        }
        createAltarHolder(game, levelId)
    }

    private fun createAltarHolder(game: GameSession, levelId: Long) {
        val ritualArea = ritualAreaRepository.findByLevelId(levelId).first()

        redisAltarHolderRepository.save(
            RedisAltarHolder(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
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
    ) = RedisAltar(
        id = Generators.timeBasedEpochGenerator().generate().toString(),
        altarId = dbAltar.inGameId!!,
        gameId = game.id!!,
        x = dbAltar.x,
        y = dbAltar.y,
        z = dbAltar.z,
        interactionRadius = dbAltar.interactionRadius!!,
        visibilityModifiers = listOf(VisibilityModifier.ALL.name).toMutableSet()
    )

}