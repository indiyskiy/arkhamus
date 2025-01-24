package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.config.UpdateUserState
import com.arkhamusserver.arkhamus.config.UserState
import com.arkhamusserver.arkhamus.logic.UserLogic
import com.arkhamusserver.arkhamus.logic.steam.SteamReaderLogic
import com.arkhamusserver.arkhamus.view.dto.user.SteamUserShortDto
import com.arkhamusserver.arkhamus.view.dto.user.UserDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
    private val userLogic: UserLogic,
    private val steamReaderLogic: SteamReaderLogic
) {
    @UpdateUserState(UserState.ONLINE)
    @GetMapping
    fun whoAmI(
    ): UserDto =
        userLogic.whoAmI()

    @UpdateUserState(UserState.ONLINE)
    @GetMapping("/friends")
    fun getServerSteamID(): List<SteamUserShortDto> {
        return steamReaderLogic.readFriendList()
    }
}