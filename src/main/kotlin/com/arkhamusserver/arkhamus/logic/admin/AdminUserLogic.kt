package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.view.dto.user.AdminUserDto
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.format.DateTimeFormatter

@Component
class AdminUserLogic(
    private val userAccountRepository: UserAccountRepository
) {
    companion object {
        private const val PATTERN = "dd.MM.yyyy HH:mm:ss"
        private val formatter = DateTimeFormatter.ofPattern(PATTERN)
    }

    fun all(): List<AdminUserDto> {
        return userAccountRepository.findAll()
            .sortedBy { it.creationTimestamp?.time ?: 0L }
            .map {
                makeUserDto(it)
            }
    }

    fun user(userId: Long): AdminUserDto {
        return makeUserDto(userAccountRepository.findById(userId).orElse(
            UserAccount(
                nickName = ""
            )
        )
        )
    }

    private fun makeUserDto(userAccount: UserAccount) = AdminUserDto(
        userId = userAccount.id!!,
        nickName = userAccount.nickName,
        email = userAccount.email!!,
        creation = dateToString(userAccount.creationTimestamp)
    )

    private fun dateToString(date: Timestamp?) =
        date?.let {
            formatter.format(it.toLocalDateTime())
        } ?: ""
}