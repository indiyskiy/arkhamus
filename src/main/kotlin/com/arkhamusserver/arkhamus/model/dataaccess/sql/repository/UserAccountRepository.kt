package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import org.springframework.data.repository.CrudRepository
import java.util.*


interface UserAccountRepository : CrudRepository<UserAccount, Long> {
    fun findByEmail(email: String): Optional<UserAccount>

    fun findBySteamId(steamId: String): Optional<UserAccount>

    override fun findById(id: Long): Optional<UserAccount>

    fun findBySteamIdIn(steamIds: List<String>): List<UserAccount>

    fun findByIdIn(ids: List<Long>): List<UserAccount>
}