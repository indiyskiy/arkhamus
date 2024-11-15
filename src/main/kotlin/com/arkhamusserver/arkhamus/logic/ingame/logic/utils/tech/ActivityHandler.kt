package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisActivityRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.redis.RedisActivity
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import org.springframework.stereotype.Component

@Component
class ActivityHandler(
    private val redisActivityRepository: RedisActivityRepository,
) {
    fun addActivity(
        gameId: Long,
        activityType: ActivityType,
        sourceUserId: Long?,
        x: Double,
        y: Double,
        z: Double,
        gameTime: Long,
        relatedGameObjectType: GameObjectType?,
        relatedGameObjectId: Long?
    ): RedisActivity {
        val activity = RedisActivity(
            id = generateRandomId(),
            gameId = gameId,
            activityType = activityType,
            sourceUserId = sourceUserId,
            x = x,
            y = y,
            z = z,
            gameTime = gameTime,
            relatedGameObjectType = relatedGameObjectType,
            relatedGameObjectId = relatedGameObjectId
        )
        redisActivityRepository.save(activity)
        return activity
    }

    fun addActivity(
        gameId: Long,
        activityType: ActivityType,
        sourceUserId: Long?,
        gameTime: Long,
        relatedGameObjectType: GameObjectType?,
        withPointRelatedObject: WithPoint
    ): RedisActivity =
        addActivity(
            gameId = gameId,
            activityType = activityType,
            sourceUserId = sourceUserId,
            x = withPointRelatedObject.x(),
            y = withPointRelatedObject.y(),
            z = withPointRelatedObject.z(),
            gameTime = gameTime,
            relatedGameObjectType = relatedGameObjectType,
            relatedGameObjectId = if (withPointRelatedObject is WithTrueIngameId) {
                withPointRelatedObject.inGameId()
            } else {
                null
            }
        )

    fun addActivity(
        gameId: Long,
        activityType: ActivityType,
        sourceUserId: Long?,
        x: Double,
        y: Double,
        z: Double,
        gameTime: Long,
    ): RedisActivity =
        addActivity(
            gameId = gameId,
            activityType = activityType,
            sourceUserId = sourceUserId,
            x = x,
            y = y,
            z = z,
            gameTime = gameTime,
            relatedGameObjectType = null,
            relatedGameObjectId = null
        )
}