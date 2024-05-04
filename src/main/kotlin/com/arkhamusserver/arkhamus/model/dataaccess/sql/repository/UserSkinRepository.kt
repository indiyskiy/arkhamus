package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.UserSkinSettings
import org.springframework.data.repository.CrudRepository
import java.util.*


interface UserSkinRepository : CrudRepository<UserSkinSettings, Long> {

    override fun findById(id: Long): Optional<UserSkinSettings>

    fun findByUserAccountId(userAccountId: Long): Optional<UserSkinSettings>
}