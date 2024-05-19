package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.Container
import org.springframework.data.repository.CrudRepository


interface ContainerRepository : CrudRepository<Container, Long> {
    fun findByLevelId(levelId: Long): List<Container>
}