package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.config.UpdateUserState
import com.arkhamusserver.arkhamus.config.CultpritsUserState
import com.arkhamusserver.arkhamus.logic.UserLogic
import com.arkhamusserver.arkhamus.logic.steam.SteamReaderLogic
import com.arkhamusserver.arkhamus.view.dto.user.SteamUserShortDto
import com.arkhamusserver.arkhamus.view.dto.user.UserDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
    private val userLogic: UserLogic,
    private val steamReaderLogic: SteamReaderLogic
) {
    @UpdateUserState(CultpritsUserState.ONLINE)
    @GetMapping
    fun whoAmI(
    ): UserDto =
        userLogic.whoAmI()

    @UpdateUserState(CultpritsUserState.ONLINE)
    @GetMapping("/friends")
    fun getServerSteamID(): List<SteamUserShortDto> {
        return steamReaderLogic.readFriendList()
    }
    
    @UpdateUserState(CultpritsUserState.ONLINE)
    @GetMapping("/friends/{steamIds}")
    fun getServerSteamID(
        @PathVariable steamIds: String
    ): List<SteamUserShortDto> {
        return steamReaderLogic.readFriendList(steamIds)
    }
}