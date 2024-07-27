package com.arkhamusserver.arkhamus.model.dataaccess

import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType

interface CountInStatistic<T> {
    fun findAll(): List<T>
    fun findByGameId(gameId: Long): List<T>
    fun redisResourceType(): RedisResourceType
}