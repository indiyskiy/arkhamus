package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.view.dto.user.AdminUserDto
import org.springframework.stereotype.Component

@Component
class AdminUserLogic(
    private val userAccountRepository: UserAccountRepository
) {
    fun all(): List<AdminUserDto> {
        return userAccountRepository.findAll().map {
            makeUserDto(it)
        }
    }
    fun user(userId: Long): AdminUserDto {
        return makeUserDto(userAccountRepository.findById(userId).orElse(UserAccount()))
    }

    private fun makeUserDto(userAccount: UserAccount) = AdminUserDto(
        userAccount.id!!,
        userAccount.nickName!!,
        userAccount.email!!
    )
}