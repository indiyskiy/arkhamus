package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.user.UserSkinSettings
import org.springframework.data.repository.CrudRepository

interface UserSkinRepository : CrudRepository<UserSkinSettings, Long>