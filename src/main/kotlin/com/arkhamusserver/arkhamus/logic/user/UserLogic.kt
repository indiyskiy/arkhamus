package com.arkhamusserver.arkhamus.logic.user

import com.arkhamusserver.arkhamus.view.dto.user.UserDto
import org.springframework.stereotype.Component

@Component
class UserLogic(
    private val currentUserService: CurrentUserService,
) {
    fun whoAmI(): UserDto {
        val currentUser = currentUserService.getCurrentUserAccount()
        return UserDto(
            currentUser.id!!,
            currentUser.steamId,
            currentUser.nickName
        )
    }
}