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

    override fun getContainers(gameId: Long): List<RedisContainer> {
        return containers.filter { it.gameId == gameId }
    }

    override fun getCrafters(gameId: Long): List<RedisCrafter> {
        return crafters.filter { it.gameId == gameId }
    }

    override fun getAltarHolder(gameId: Long): RedisAltarHolder {
        TODO("Not yet implemented")
    }

    override fun getAltarPolling(gameId: Long): RedisAltarPolling? {
        TODO("Not yet implemented")
    }

    override fun getAltars(gameId: Long): Map<Long, RedisAltar> {
        TODO("Not yet implemented")
    }

    override fun getLanterns(gameId: Long): List<RedisLantern> {
        TODO("Not yet implemented")
    }

    override fun getVoteSpots(gameId: Long): List<RedisVoteSpot> {
        TODO("Not yet implemented")
    }

    override fun getUserVoteSpots(gameId: Long): List<RedisUserVoteSpot> {
        TODO("Not yet implemented")
    }

    override fun getThresholds(gameId: Long): List<RedisThreshold> {
        TODO("Not yet implemented")
    }

    override fun getDoors(gameId: Long): List<RedisDoor> {
        TODO("Not yet implemented")
    }

    override fun getCastAbilities(gameId: Long): List<RedisAbilityCast> {
        TODO("Not yet implemented")
    }

    override fun getCraftProcess(gameId: Long): List<RedisCraftProcess> {
        TODO("Not yet implemented")
    }

    override fun getTimeEvents(gameId: Long): List<RedisTimeEvent> {
        return timeEvents[gameId] ?: emptyList()
    }

    override fun getShortTimeEvents(gameId: Long): List<RedisShortTimeEvent> {
        TODO("Not yet implemented")
    }

    override fun getZones(gameId: Long): List<RedisLevelZone> {
        TODO("Not yet implemented")
    }

    override fun getTetragons(gameId: Long): List<RedisLevelZoneTetragon> {
        TODO("Not yet implemented")
    }

    override fun getClues(gameId: Long): List<RedisClue> {
        TODO("Not yet implemented")
    }

    override fun getQuests(gameId: Long): List<RedisQuest> {
        TODO("Not yet implemented")
    }

    override fun getQuestGivers(gameId: Long): List<RedisQuestGiver> {
        TODO("Not yet implemented")
    }

    override fun getQuestRewards(gameId: Long): List<RedisQuestReward> {
        TODO("Not yet implemented")
    }

    override fun getUserQuestProrgesses(gameId: Long): List<RedisUserQuestProgress> {
        TODO("Not yet implemented")
    }

    override fun getEllipses(gameId: Long): List<RedisLevelZoneEllipse> {
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