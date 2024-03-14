package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class MockRedisDataAccess : RedisDataAccess {

    private var gameUsers = listOf<RedisGameUser>()
    private var games = listOf<RedisGame>()
    private var containers = listOf<RedisContainer>()
    private var globalGameDatas = listOf<GlobalGameData>()
    private var timeEvents = mutableMapOf<Long, List<RedisTimeEvent>>()

    override fun getGameUser(userId: Long?, gameId: Long?): RedisGameUser {
        return gameUsers.find { it.userId == userId && it.gameId == gameId }!!
    }

    override fun getGameUsers(gameId: Long?): List<RedisGameUser> {
        return gameUsers.filter { it.gameId == gameId }
    }

    override fun getGame(gameId: Long): RedisGame {
        return games.find { it.gameId == gameId }!!
    }

    override fun getContainer(containerId: Long, gameId: Long): RedisContainer {
        return containers.find { it.containerId == containerId && it.gameId == gameId }!!
    }

    override fun getGameContainers(gameId: Long): List<RedisContainer> {
        return containers.filter { it.gameId == gameId }
    }

    override fun getTimeEvents(gameId: Long): List<RedisTimeEvent> {
        return timeEvents[gameId] ?: emptyList()
    }

    fun setUp(globalGameDatas: List<GlobalGameData>) {
        this.globalGameDatas = globalGameDatas
        games = globalGameDatas.map { it.game }
        gameUsers = globalGameDatas.flatMap { it.users.values }
        containers = globalGameDatas.flatMap { it.containers.values }
    }

    fun cleanUp() {
        gameUsers = listOf()
        games = listOf()
        containers = listOf()
        globalGameDatas = listOf()
        timeEvents = mutableMapOf()
    }
}