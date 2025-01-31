package com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
sealed interface MyCrudRepository<T> : NonGenericMyCrudRepository,
    CrudRepository<T, String> {
    override fun findByGameId(gameId: Long): List<T>
}