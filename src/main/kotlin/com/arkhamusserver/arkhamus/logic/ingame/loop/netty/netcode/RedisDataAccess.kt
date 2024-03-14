package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent

interface RedisDataAccess {
    fun getGameUser(userId: Long?, gameId: Long?): RedisGameUser
    fun getGameUsers(gameId: Long?): List<RedisGameUser>
    fun getGame(gameId: Long): RedisGame
    fun getContainer(containerId: Long, gameId: Long): RedisContainer
    fun getGameContainers(gameId: Long): List<RedisContainer>
    fun getTimeEvents(gameId: Long): List<RedisTimeEvent>
}

fun RedisDataAccess.loadGlobalGameData(game: RedisGame): GlobalGameData {
    val gameId = game.id.toLong()
    val allUsers = getGameUsers(gameId)
    val allContainers = getGameContainers(gameId)
    val allEvents = getTimeEvents(gameId)
    return GlobalGameData(game).apply {
        this.users = allUsers.associateBy { it.userId }
        this.containers = allContainers.associateBy { it.containerId }
        this.timeEvents = allEvents
    }
}

fun RedisDataAccess.getOtherGameUsers(userId: Long?, gameId: Long?): List<RedisGameUser> =
    getGameUsers(gameId).filter { it.userId != userId }

fun RedisDataAccess.getOtherGameUsers(userId: String, gameId: Long) =
    getGameUsers(gameId).filter { it.id != userId }