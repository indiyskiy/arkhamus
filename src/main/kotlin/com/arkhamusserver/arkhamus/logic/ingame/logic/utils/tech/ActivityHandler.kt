package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisActivityRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameActivityRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.GameActivity
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.redis.RedisActivity
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ActivityHandler(
    private val redisActivityRepository: RedisActivityRepository,
    private val gameActivityRepository: GameActivityRepository,
    private val gameSessionRepository: GameSessionRepository
) {
    companion object {
        private var logger = LoggerFactory.getLogger(ActivityHandler::class.java)
    }

    fun addActivity(
        gameId: Long,
        activityType: ActivityType,
        sourceUserId: Long?,
        x: Double,
        y: Double,
        z: Double,
        gameTime: Long,
        relatedGameObjectType: GameObjectType?,
        relatedGameObjectId: Long?,
        relatedEventId: Long?,
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
            relatedGameObjectId = relatedGameObjectId,
            relatedEventId = relatedEventId
        )
        redisActivityRepository.save(activity)
        logger.info("added activity: ${activity.activityType.name}")
        return activity
    }

    fun addUserNotTargetActivity(
        gameId: Long,
        activityType: ActivityType,
        sourceUser: RedisGameUser,
        gameTime: Long,
        relatedEventId: Long?,
    ): RedisActivity =
        addActivity(
            gameId = gameId,
            activityType = activityType,
            sourceUserId = sourceUser.inGameId(),
            x = sourceUser.x(),
            y = sourceUser.y(),
            z = sourceUser.z(),
            gameTime = gameTime,
            relatedGameObjectType = null,
            relatedGameObjectId = null,
            relatedEventId = relatedEventId
        )

    fun addUserWithTargetActivity(
        gameId: Long,
        activityType: ActivityType,
        sourceUser: RedisGameUser,
        gameTime: Long,
        relatedGameObjectType: GameObjectType?,
        withTrueIngameId: WithTrueIngameId?,
        relatedEventId: Long?,
    ): RedisActivity =
        addActivity(
            gameId = gameId,
            activityType = activityType,
            sourceUserId = sourceUser.inGameId(),
            x = sourceUser.x(),
            y = sourceUser.y(),
            z = sourceUser.z(),
            gameTime = gameTime,
            relatedGameObjectType = relatedGameObjectType,
            relatedGameObjectId = withTrueIngameId?.inGameId(),
            relatedEventId = relatedEventId
        )

    @Transactional
    fun saveAll(gameId: Long) {
        val redisActivities = redisActivityRepository.findByGameId(gameId)
        val gameSession = gameSessionRepository.findById(gameId).orElse(null)
        if (gameSession == null) {
            redisActivityRepository.deleteAll(redisActivities)
            return
        }
        val activities = redisActivities.map { redisActivity ->
            GameActivity(
                x = redisActivity.x,
                y = redisActivity.y,
                z = redisActivity.z,
                gameTime = redisActivity.gameTime,
                activityType = redisActivity.activityType,
                relatedGameObjectType = redisActivity.relatedGameObjectType,
                relatedGameObjectId = redisActivity.relatedGameObjectId,
                relatedEventId = redisActivity.relatedEventId,
                gameSession = gameSession,
                userOfGameSession = gameSession.usersOfGameSession.first { it.userAccount.id == redisActivity.sourceUserId },
            )
        }
        logger.info("saving ${activities.size} activities")
        gameActivityRepository.saveAll(activities)
        redisActivityRepository.deleteAll(redisActivities)
    }

    fun saveHeartbeatForUsers(data: GlobalGameData) {
        data.users.forEach {
            addUserNotTargetActivity(
                gameId = data.game.inGameId(),
                activityType = ActivityType.HEARTBEAT,
                sourceUser = it.value,
                gameTime = data.game.globalTimer,
                relatedEventId = null
            )
        }
    }
}