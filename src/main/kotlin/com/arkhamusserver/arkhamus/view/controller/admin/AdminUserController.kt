package com.arkhamusserver.arkhamus.view.controller.admin

import com.arkhamusserver.arkhamus.logic.admin.AdminUserLogic
import com.arkhamusserver.arkhamus.view.dto.user.AdminUserDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/user")
class AdminUserController (
        private val adminUserLogic: AdminUserLogic
    ) {
        @GetMapping
        fun allUsers(
        ): List<AdminUserDto> =
            adminUserLogic.all()
}