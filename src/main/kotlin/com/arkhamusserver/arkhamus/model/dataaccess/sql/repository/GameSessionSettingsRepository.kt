package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.GameSessionSettings
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GameSessionSettingsRepository : CrudRepository<GameSessionSettings, Long> {
    override fun findById(id: Long): Optional<GameSessionSettings>
}