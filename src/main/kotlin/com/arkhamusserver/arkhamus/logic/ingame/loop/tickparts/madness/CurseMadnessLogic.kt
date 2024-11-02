package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithGameTags
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class CurseMadnessLogic(
    private val redisContainerRepository: RedisContainerRepository,
    private val redisCrafterRepository: RedisCrafterRepository,
    private val userLocationHandler: UserLocationHandler,
) {
    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    fun curseSomething(user: RedisGameUser, data: GlobalGameData, timePassedMillis: Long): Boolean {
        val validCrafters = nonCursedOpenCrafters(data, user)
        val validContainers = nonCursedOpenContainer(data, user)
        val allObjects: List<WithGameTags> = validCrafters + validContainers
        if (allObjects.isNotEmpty()) {
            curseRandomItem(allObjects)
            return true
        }
        return false
    }

    private fun curseRandomItem(allObjects: List<WithGameTags>) {
        val randomObject = allObjects.random(random)
        randomObject.gameTags().add(InGameObjectTag.PEEKABOO_CURSE.name)
        if (randomObject is RedisCrafter) {
            redisCrafterRepository.save(randomObject)
        } else {
            if (randomObject is RedisContainer) {
                redisContainerRepository.save(randomObject)
            }
        }
    }

    private fun nonCursedOpenContainer(
        data: GlobalGameData,
        user: RedisGameUser
    ): List<RedisContainer> = data.containers.values.filter {
        userLocationHandler.userCanSeeTarget(
            user,
            it,
            data.levelGeometryData,
            true
        ) &&
                it.state == MapObjectState.ACTIVE &&
                !it.gameTags.contains(InGameObjectTag.PEEKABOO_CURSE.name)
    }.toList()

    private fun nonCursedOpenCrafters(
        data: GlobalGameData,
        user: RedisGameUser
    ): List<RedisCrafter> = data.crafters.values.filter {
        userLocationHandler.userCanSeeTarget(
            user,
            it,
            data.levelGeometryData,
            true
        ) &&
                it.state == MapObjectState.ACTIVE &&
                !it.gameTags.contains(InGameObjectTag.PEEKABOO_CURSE.name)
    }.toList()
}