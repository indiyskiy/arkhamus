package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.config.UserState
import com.arkhamusserver.arkhamus.model.dataaccess.UserStatusService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UserStatusScheduler(
    private val userStatusService: UserStatusService
) {
    @Scheduled(fixedRate = 60_000) // Runs every minute
    fun checkAndUpdateUserStatus() {
        val currentTime = System.currentTimeMillis()
        val userStatuses = userStatusService.getAllStatuses()

        userStatuses.forEach {
            val minutesInactive = currentTime - it.lastActive

            val newState = if (minutesInactive > UserStatusService.OFFLINE_TIME) {
                UserState.AFK
            } else {
                if (minutesInactive > UserStatusService.AFK_TIME) {
                    UserState.AFK
                } else {
                    it.userState
                }
            }
            if (it.userState != newState) {
                userStatusService.setUserStatusForce(it.userId, newState)
            }
        }
    }
}