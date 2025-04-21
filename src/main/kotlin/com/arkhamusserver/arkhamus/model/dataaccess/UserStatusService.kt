package com.arkhamusserver.arkhamus.model.dataaccess

import com.arkhamusserver.arkhamus.config.CultpritsUserState
import com.arkhamusserver.arkhamus.model.UserStateHolder
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class UserStatusService {
    companion object {
        const val MAX_TIME_MILLIS = 1000 * 60 / 2 //30 sec
        const val AFK_TIME = 1000 * 60 * 5 //5 min
        const val OFFLINE_TIME = 1000 * 60 * 30 //30 min
    }

    private val userStatusMap: ConcurrentHashMap<Long, UserStateHolder> = ConcurrentHashMap()

    fun updateUserStatus(userId: Long, state: CultpritsUserState, force: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        userStatusMap[userId] = updatedState(
            userId,
            getUserStatus(userId),
            state,
            currentTime,
            force
        )
    }

    private fun updatedState(
        userId: Long,
        oldHolder: UserStateHolder?,
        state: CultpritsUserState,
        currentTime: Long,
        force: Boolean = false
    ): UserStateHolder {
        if (oldHolder == null) {
            return buildNewState(userId, state, currentTime)
        }
        if (state == oldHolder.userState) {
            return buildNewState(userId, state, currentTime)
        }
        if (state.forceUpdate || force) {
            return buildNewState(userId, state, currentTime)
        }
        if (state.priority >= oldHolder.userState.priority) {
            return buildNewState(userId, state, currentTime)
        }
        if (currentTime - (oldHolder.lastActive) > MAX_TIME_MILLIS) {
            return buildNewState(userId, state, currentTime)
        }
        return oldHolder
    }

    fun getUserStatus(userId: Long): UserStateHolder {
        return userStatusMap[userId] ?: offline(userId)
    }

    private fun offline(userId: Long): UserStateHolder {
        return buildNewState(userId, CultpritsUserState.OFFLINE, 0L)
    }

    fun getAllStatuses(): List<UserStateHolder> {
        return userStatusMap.values.toList()
    }

    fun setUserStatusForce(userId: Long, state: CultpritsUserState) {
        userStatusMap[userId] =
            buildNewState(userId, state, userStatusMap[userId]?.lastActive ?: System.currentTimeMillis())
    }

    private fun buildNewState(
        userId: Long,
        state: CultpritsUserState,
        currentTime: Long
    ): UserStateHolder = UserStateHolder(userId, state, currentTime)
}