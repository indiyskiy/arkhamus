package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.view.dto.admin.AdminRedisResourcesInfoDto
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceDto
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType.GAME
import org.springframework.stereotype.Component

@Component
class AdminRedisResourcesLogic(
    private val gameRepository: RedisGameRepository,
    private val countInStatistics: List<CountInStatistic<*>>,

    ) {
    fun info(): AdminRedisResourcesInfoDto {
        return AdminRedisResourcesInfoDto(
            listOf(
                gameRepository.findAll().mapInfoList(GAME),
            ) + countInStatistics.map { it.findAll() to it.redisResourceType() }.map {
                it.first.mapInfoList(it.second)
            }
        )
    }

    fun info(gameId: Long): AdminRedisResourcesInfoDto {
        return AdminRedisResourcesInfoDto(
            listOf(
                gameRepository.findByGameId(gameId).mapInfo(GAME),
            ) + countInStatistics.map { it.findByGameId(gameId) to it.redisResourceType() }.map {
                it.first.mapInfoList(it.second)
            }
        )
    }

    private fun <T> Iterable<T>.mapInfoList(type: RedisResourceType): RedisResourceDto {
        return RedisResourceDto(type, this.toList().size)
    }

    private fun Any?.mapInfo(type: RedisResourceType): RedisResourceDto {
        return RedisResourceDto(type, this?.let { 1 } ?: 0)
    }
}




