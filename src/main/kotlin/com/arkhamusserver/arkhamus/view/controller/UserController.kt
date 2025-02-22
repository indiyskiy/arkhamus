package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.config.CultpritsUserState
import com.arkhamusserver.arkhamus.config.UpdateUserState
import com.arkhamusserver.arkhamus.logic.user.UserLogic
import com.arkhamusserver.arkhamus.logic.user.relations.UserRelationLogic
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
    private val relationsLogic: UserRelationLogic
) {
    @UpdateUserState(CultpritsUserState.ONLINE)
    @GetMapping
    fun whoAmI(
    ): UserDto =
        userLogic.whoAmI()
    
    @UpdateUserState(CultpritsUserState.ONLINE)
    @GetMapping("/friends/{steamIds}")
    fun getFriends(
        @PathVariable steamIds: String
    ): List<SteamUserShortDto> {
        return relationsLogic.readFriendList(steamIds)
    }
}