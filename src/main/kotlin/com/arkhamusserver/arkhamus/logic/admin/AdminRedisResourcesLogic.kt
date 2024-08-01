package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.NonGenericMyCrudRepository
import com.arkhamusserver.arkhamus.view.dto.admin.AdminRedisResourcesInfoDto
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceDto
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

@Component
class AdminRedisResourcesLogic(
    private val gameRepository: RedisGameRepository,
    private val toCount: List<NonGenericMyCrudRepository>,
) {
    fun info(): AdminRedisResourcesInfoDto {
        val games = gameRepository.findAll()
        val statistic =
            toCount.map { countInStatistic ->
                if (countInStatistic is CrudRepository<*, *>) {
                    countInStatistic.findAll() to countName(countInStatistic)
                } else {
                    emptyList<String>() to "-"
                }
            }
        val gameDto = games.mapInfoList("Game")
        val statisticDto = statistic.map { it.first.mapInfoList(it.second) }
        val fullStatisticDtoList = statisticDto + gameDto
        return AdminRedisResourcesInfoDto(fullStatisticDtoList)
    }

    fun info(gameId: Long): AdminRedisResourcesInfoDto {
        return AdminRedisResourcesInfoDto(
            listOf(
                gameRepository.findByGameId(gameId).mapInfo("Game"),
            ) + toCount.map { it.findByGameId(gameId) to it.javaClass.simpleName }.map {
                it.first.mapInfoList(it.second)
            }
        )
    }

    private fun <T> Iterable<T>.mapInfoList(type: String): RedisResourceDto {
        return RedisResourceDto(type, this.toList().size)
    }

    private fun Any?.mapInfo(type: String): RedisResourceDto {
        return RedisResourceDto(type, this?.let { 1 } ?: 0)
    }

    private fun countName(nonGenericCountInStatistic: NonGenericMyCrudRepository): String =
        nonGenericCountInStatistic.javaClass.annotatedInterfaces.first {
            val name = it.type.typeName
            name.contains("Redis") && name.contains("Repository")
        }.type.typeName
            .replace("Redis", "")
            .replace("Repository", "")
            .replace("com.arkhamusserver.arkhamus.model.dataaccess.redis.", "")
}




