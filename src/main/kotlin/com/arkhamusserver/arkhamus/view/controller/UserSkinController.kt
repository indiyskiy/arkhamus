package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.logic.user.UserSkinLogic
import com.arkhamusserver.arkhamus.view.dto.UserSkinDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/skin")
class UserSkinController(
    private val userSkinLogic: UserSkinLogic
) {
    @GetMapping
    fun userSkin(): UserSkinDto =
        userSkinLogic.userSkin()

    @PutMapping
    fun userSkin(
        @RequestBody userSkin: UserSkinDto
    ): UserSkinDto =
        userSkinLogic.updateUserSkin(userSkin)

}