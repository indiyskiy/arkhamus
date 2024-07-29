package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import org.springframework.data.repository.CrudRepository
import java.util.*


interface UserAccountRepository : CrudRepository<UserAccount, Long> {
    fun findByEmail(email: String): Optional<UserAccount>

    override fun findById(id: Long): Optional<UserAccount>
}