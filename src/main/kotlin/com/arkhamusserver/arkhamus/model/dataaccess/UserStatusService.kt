package com.arkhamusserver.arkhamus.model.dataaccess

import com.arkhamusserver.arkhamus.config.UserState
import com.arkhamusserver.arkhamus.model.UserStateHolder
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class UserStatusService {
    companion object {
        const val MAX_TIME_MILLIS = 1000 * 60 / 2
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
            return UserStateHolder(userId, state, currentTime)
        }
        if (state.forceUpdate) {
            return UserStateHolder(userId, state, currentTime)
        }
        if (state.priority >= oldHolder.userState.priority) {
            return UserStateHolder(userId, state, currentTime)
        }
        if (currentTime - (oldHolder.lastActive) > MAX_TIME_MILLIS) {
            return UserStateHolder(userId, state, currentTime)
        }
        return oldHolder
    }

    fun getUserStatus(userId: Long): UserStateHolder {
        return userStatusMap[userId] ?: offline(userId)
    }

    private fun offline(userId: Long): UserStateHolder {
        return UserStateHolder(userId, UserState.OFFLINE, 0L)
    }
}