package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.TextKey
import org.springframework.data.repository.CrudRepository

interface TextKeyRepository : CrudRepository<TextKey, Long>