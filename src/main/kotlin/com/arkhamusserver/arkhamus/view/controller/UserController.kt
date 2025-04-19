package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.config.CultpritsUserState
import com.arkhamusserver.arkhamus.config.UpdateUserState
import com.arkhamusserver.arkhamus.logic.user.UserLogic
import com.arkhamusserver.arkhamus.logic.user.relations.UserRelationLogic
import com.arkhamusserver.arkhamus.view.dto.user.RelatedUserDto
import com.arkhamusserver.arkhamus.view.dto.user.UserDto
import org.springframework.web.bind.annotation.*

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
    ): List<RelatedUserDto> {
        return relationsLogic.readFriendList(steamIds)
    }
    @UpdateUserState(CultpritsUserState.ONLINE)
    @PostMapping("/friends/{userId}")
    fun makeFriend(
        @PathVariable userId: Long
    ): RelatedUserDto {
        return relationsLogic.makeFriend(userId)
    }
}