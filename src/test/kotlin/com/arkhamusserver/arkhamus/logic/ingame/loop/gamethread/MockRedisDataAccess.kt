package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.model.redis.*
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class MockRedisDataAccess : RedisDataAccess {

    private var gameUsers = listOf<RedisGameUser>()
    private var games = listOf<RedisGame>()
    private var containers = listOf<RedisContainer>()
    private var crafters = listOf<RedisCrafter>()
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

    override fun getCrafter(crafterId: Long, gameId: Long): RedisCrafter {
        return crafters.find { it.crafterId == crafterId && it.gameId == gameId }!!
    }

    override fun getGameContainers(gameId: Long): List<RedisContainer> {
        return containers.filter { it.gameId == gameId }
    }

    override fun getGameCrafters(gameId: Long): List<RedisCrafter> {
        return crafters.filter { it.gameId == gameId }
    }

    override fun getLantern(lanternId: Long, gameId: Long): RedisLantern {
        TODO("Not yet implemented")
    }
    override fun getAltar(altarId: Long, gameId: Long): RedisAltar {
        TODO("Not yet implemented")
    }

    override fun getGameAltars(gameId: Long): List<RedisAltar> {
        TODO("Not yet implemented")
    }

    override fun getGameLanterns(gameId: Long): List<RedisLantern> {
        TODO("Not yet implemented")
    }

    override fun getCastedAbilities(gameId: Long): List<RedisAbilityCast> {
        TODO("Not yet implemented")
    }

    override fun getCraftProcess(gameId: Long): List<RedisCraftProcess> {
        TODO("Not yet implemented")
    }

    override fun getTimeEvents(gameId: Long): List<RedisTimeEvent> {
        return timeEvents[gameId] ?: emptyList()
    }

    override fun deleteGame(gameId: Long) {
        TODO("Not yet implemented")
    }

    override fun deleteGameUsers(gameId: Long) {
        TODO("Not yet implemented")
    }

    override fun deleteContainers(gameId: Long) {
        TODO("Not yet implemented")
    }

    override fun deleteLanterns(gameId: Long) {
        TODO("Not yet implemented")
    }

    override fun deleteTimeEvents(gameId: Long) {
        TODO("Not yet implemented")
    }

    fun setUp(globalGameDatas: List<GlobalGameData>) {
        this.globalGameDatas += globalGameDatas
        games = this.globalGameDatas.map { it.game }
        gameUsers = this.globalGameDatas.flatMap { it.users.values }
        containers = this.globalGameDatas.flatMap { it.containers.values }
    }

    fun cleanUp() {
        gameUsers = listOf()
        games = listOf()
        containers = listOf()
        globalGameDatas = listOf()
        timeEvents = mutableMapOf()
    }
}