package com.arkhamusserver.arkhamus.model.dataaccess

import com.arkhamusserver.arkhamus.config.UserState
import com.arkhamusserver.arkhamus.model.UserStateHolder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class UserStatusService {
    companion object {
        const val MAX_TIME_MILLIS = 1000 * 60 / 2 //30 sec
        const val AFK_TIME = 1000 * 60 * 5 //5 min
        const val OFFLINE_TIME = 1000 * 60 * 30 //30 min

        private val logger = LoggerFactory.getLogger(UserStatusService::class.java)
    }

    private val userStatusMap: ConcurrentHashMap<Long, UserStateHolder> = ConcurrentHashMap()

    fun updateUserStatus(userId: Long, state: UserState) {
        val currentTime = System.currentTimeMillis()
        userStatusMap[userId] = updatedState(
            userId,
            getUserStatus(userId),
            state,
            currentTime
        )
    }

    private fun updatedState(
        userId: Long,
        oldHolder: UserStateHolder?,
        state: UserState,
        currentTime: Long
    ): UserStateHolder {
        if (oldHolder == null) {
            logger.info("set up new user status: $userId, $state")
            return buildNewState(userId, state, currentTime)
        }
        if (state == oldHolder.userState) {
            return buildNewState(userId, state, currentTime)
        }
        if (state.forceUpdate) {
            logger.info("force update new user state: $userId, $state")
            return buildNewState(userId, state, currentTime)
        }
        if (state.priority >= oldHolder.userState.priority) {
            logger.info("priority update new user state: $userId, $state")
            return buildNewState(userId, state, currentTime)
        }
        if (currentTime - (oldHolder.lastActive) > MAX_TIME_MILLIS) {
            logger.info("time update new user state: $userId, $state")
            return buildNewState(userId, state, currentTime)
        }
        return oldHolder
    }

    fun getUserStatus(userId: Long): UserStateHolder {
        return userStatusMap[userId] ?: offline(userId)
    }

    private fun offline(userId: Long): UserStateHolder {
        return buildNewState(userId, UserState.OFFLINE, 0L)
    }

    fun getAllStatuses(): List<UserStateHolder> {
        return userStatusMap.values.toList()
    }

    fun setUserStatusForce(userId: Long, state: UserState) {
        logger.info("timeout update new user state: $userId, $state")
        userStatusMap[userId] =
            buildNewState(userId, state, userStatusMap[userId]?.lastActive ?: System.currentTimeMillis())
    }

    private fun buildNewState(
        userId: Long,
        state: UserState,
        currentTime: Long
    ): UserStateHolder = UserStateHolder(userId, state, currentTime)
}