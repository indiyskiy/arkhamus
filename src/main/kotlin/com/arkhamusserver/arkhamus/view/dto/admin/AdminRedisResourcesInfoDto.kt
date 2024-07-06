package com.arkhamusserver.arkhamus.view.dto.admin

data class AdminRedisResourcesInfoDto(
    val redisResourceDtos: List<RedisResourceDto>
)

data class RedisResourceDto(
    val type: RedisResourceType,
    val size: Int
)