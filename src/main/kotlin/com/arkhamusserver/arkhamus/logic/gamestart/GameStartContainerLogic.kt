package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.utils.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ContainerRepository
import com.arkhamusserver.arkhamus.model.database.entity.Container
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.ContainerAffectModifiers
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import org.springframework.stereotype.Component

@Component
class GameStartContainerLogic(
    private val redisContainerRepository: RedisContainerRepository,
    private val containerRepository: ContainerRepository,
    private val gameRelatedIdSource: GameRelatedIdSource,
) {
    fun createContainers(
        levelId: Long,
        game: GameSession
    ) {
        val allLevelContainers = containerRepository.findByLevelId(levelId)
        allLevelContainers.forEach { dbContainer ->
            val modifiers = listOf(ContainerAffectModifiers.FULL_RANDOM)
            with(createContainer(game, dbContainer, modifiers)) {
                redisContainerRepository.save(this)
            }
        }
    }

    private fun createContainer(
        game: GameSession,
        dbContainer: Container,
        modifiers: List<ContainerAffectModifiers>
    ) = RedisContainer(
        id = gameRelatedIdSource.getId(game.id!!, dbContainer.inGameId!!),
        containerId = dbContainer.inGameId!!,
        gameId = game.id!!
    ).apply {
        this.x = dbContainer.x!!
        this.y = dbContainer.y!!
        this.interactionRadius = dbContainer.interactionRadius!!
        this.items = randomizeItems(modifiers)
    }

    private fun randomizeItems(modifiers: List<ContainerAffectModifiers>): Map<String, Long> {
        return when (modifiers.first()) {
            ContainerAffectModifiers.FULL_RANDOM -> {
                val items = Item.values().filter {
                    it.getItemType() in setOf(ItemType.LOOT, ItemType.RARE_LOOT)
                }.shuffled(GameStartLogic.random).subList(0, GameStartLogic.random.nextInt(3) + 1)
                items.associate { it.getId().toString() to (GameStartLogic.random.nextLong(3) + 1) }
            }
        }
    }
}