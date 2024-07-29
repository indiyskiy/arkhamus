package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.game.Role
import org.springframework.data.repository.CrudRepository
import java.util.*


interface RoleRepository : CrudRepository<Role, Long> {
    fun findByName(email: String): Optional<Role>

    override fun findById(id: Long): Optional<Role>
}