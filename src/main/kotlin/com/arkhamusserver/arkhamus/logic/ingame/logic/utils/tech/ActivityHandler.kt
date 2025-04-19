package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameActivityRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameActivityRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.GameActivity
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.ingame.InGameActivity
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ActivityHandler(
    private val inGameActivityRepository: InGameActivityRepository,
    private val gameActivityRepository: GameActivityRepository,
    private val gameSessionRepository: GameSessionRepository
) {
    companion object {
        private var logger = LoggingUtils.getLogger<ActivityHandler>()
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
    ): InGameActivity {
        val activity = InGameActivity(
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
        inGameActivityRepository.save(activity)
        LoggingUtils.info(logger, LoggingUtils.EVENT_SYSTEM, "added activity: ${activity.activityType.name}")
        return activity
    }

    fun addUserNotTargetActivity(
        gameId: Long,
        activityType: ActivityType,
        sourceUser: InGameUser,
        gameTime: Long,
        relatedEventId: Long?,
    ): InGameActivity =
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
        sourceUser: InGameUser,
        gameTime: Long,
        relatedGameObjectType: GameObjectType?,
        withTrueIngameId: WithTrueIngameId?,
        relatedEventId: Long?,
    ): InGameActivity =
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
        val inGameActivities = inGameActivityRepository.findByGameId(gameId)
        val gameSession = gameSessionRepository.findById(gameId).orElse(null)
        if (gameSession == null) {
            inGameActivityRepository.deleteAll(inGameActivities)
            return
        }
        val activities = inGameActivities.map { inGameActivity ->
            GameActivity(
                x = inGameActivity.x,
                y = inGameActivity.y,
                z = inGameActivity.z,
                gameTime = inGameActivity.gameTime,
                activityType = inGameActivity.activityType,
                relatedGameObjectType = inGameActivity.relatedGameObjectType,
                relatedGameObjectId = inGameActivity.relatedGameObjectId,
                relatedEventId = inGameActivity.relatedEventId,
                gameSession = gameSession,
                userOfGameSession = gameSession.usersOfGameSession.first { it.userAccount.id == inGameActivity.sourceUserId },
            )
        }
        LoggingUtils.info(logger, LoggingUtils.EVENT_SYSTEM, "saving ${activities.size} activities")
        gameActivityRepository.saveAll(activities)
        inGameActivityRepository.deleteAll(inGameActivities)
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
