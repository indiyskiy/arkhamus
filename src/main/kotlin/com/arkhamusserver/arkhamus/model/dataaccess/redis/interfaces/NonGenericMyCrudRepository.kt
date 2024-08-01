package com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces

import java.lang.reflect.ParameterizedType

interface NonGenericMyCrudRepository {
    fun findByGameId(gameId: Long): Iterable<*>
    fun redisResourceType(): String {
        return ((javaClass
            .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>).typeName
    }
}