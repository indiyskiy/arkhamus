package com.arkhamusserver.arkhamus.config.repository

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InRamGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.NonGenericMyCrudRepository
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import jakarta.annotation.PostConstruct
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class InGameRepositoryCleaner(
    private val inRamGameRepository: InRamGameRepository,
    private val toDelete: List<NonGenericMyCrudRepository>,
) {

    companion object {
        private val logger = LoggingUtils.getLogger<InGameRepositoryCleaner>()
    }

    @PostConstruct
    @Transactional
    fun cleanAll() {
        inRamGameRepository.deleteAll()
        toDelete.forEach { repo ->
            if (repo is CrudRepository<*, *>) {
                repo.deleteAll()
            }
        }
    }

    @Transactional
    fun cleanGame(gameId: Long) {
        toDelete.forEach { repo ->
            val entities = repo.findByGameId(gameId)
            if (repo is CrudRepository<*, *>) {
                (repo as CrudRepository<Any, String>).deleteAll(entities as MutableIterable<*>)
            }
        }
        inRamGameRepository.findByGameId(gameId).firstOrNull()?.let { inRamGameRepository.delete(it) } ?: {
            logger.warn("Game repository contains no game for id $gameId")
        }
    }

}