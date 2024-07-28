package com.arkhamusserver.arkhamus.model.dataaccess

import java.lang.reflect.ParameterizedType

interface CountInStatistic<T> {
    fun findAll(): Iterable<T>
    fun findByGameId(gameId: Long): Iterable<T>
    fun redisResourceType(): String {
        return ((javaClass
            .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>).typeName
    }
}