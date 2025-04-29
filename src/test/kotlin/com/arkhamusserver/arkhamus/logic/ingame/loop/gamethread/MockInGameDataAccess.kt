package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.InGameDataAccess
import com.arkhamusserver.arkhamus.model.ingame.*
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class MockInGameDataAccess : InGameDataAccess {

    private var gameUsers = listOf<InGameUser>()
    private var games = listOf<InRamGame>()
    private var containers = listOf<InGameContainer>()
    private var crafters = listOf<InGameCrafter>()
    private var globalGameDatas = listOf<GlobalGameData>()
    private var timeEvents = mutableMapOf<Long, List<InGameTimeEvent>>()

    override fun getGameUser(userId: Long?, gameId: Long?): InGameUser {
        return gameUsers.find { it.userId == userId && it.gameId == gameId }!!
    }

    override fun getGameUsers(gameId: Long?): List<InGameUser> {
        return gameUsers.filter { it.gameId == gameId }
    }

    override fun getGame(gameId: Long): InRamGame {
        return games.find { it.gameId == gameId }!!
    }

    override fun getContainers(gameId: Long): List<InGameContainer> {
        return containers.filter { it.gameId == gameId }
    }

    override fun getCrafters(gameId: Long): List<InGameCrafter> {
        return crafters.filter { it.gameId == gameId }
    }

    override fun getAltarHolder(gameId: Long): InGameAltarHolder {
        TODO("Not yet implemented")
    }

    override fun getAltarPolling(gameId: Long): InGameAltarPolling? {
        TODO("Not yet implemented")
    }

    override fun getAltars(gameId: Long): List<InGameAltar> {
        TODO("Not yet implemented")
    }

    override fun getLanterns(gameId: Long): List<InGameLantern> {
        TODO("Not yet implemented")
    }

    override fun getVoteSpots(gameId: Long): List<InGameVoteSpot> {
        TODO("Not yet implemented")
    }

    override fun getUserVoteSpots(gameId: Long): List<InGameUserVoteSpot> {
        TODO("Not yet implemented")
    }

    override fun getThresholds(gameId: Long): List<InGameThreshold> {
        TODO("Not yet implemented")
    }

    override fun getDoors(gameId: Long): List<InGameDoor> {
        TODO("Not yet implemented")
    }

    override fun getCastAbilities(gameId: Long): List<InGameAbilityActiveCast> {
        TODO("Not yet implemented")
    }

    override fun getAbilityCooldowns(gameId: Long): List<InGameAbilityCooldown> {
        TODO("Not yet implemented")
    }

    override fun getCraftProcess(gameId: Long): List<InGameCraftProcess> {
        TODO("Not yet implemented")
    }

    override fun getTimeEvents(gameId: Long): List<InGameTimeEvent> {
        return timeEvents[gameId] ?: emptyList()
    }

    override fun getShortTimeEvents(gameId: Long): List<InGameShortTimeEvent> {
        TODO("Not yet implemented")
    }

    override fun getZones(gameId: Long): List<InGameLevelZone> {
        TODO("Not yet implemented")
    }

    override fun getTetragons(gameId: Long): List<InGameLevelZoneTetragon> {
        TODO("Not yet implemented")
    }

    override fun getClues(gameId: Long): CluesContainer {
        TODO("Not yet implemented")
    }

    override fun getQuests(gameId: Long): List<InGameQuest> {
        TODO("Not yet implemented")
    }

    override fun getQuestGivers(gameId: Long): List<InGameQuestGiver> {
        TODO("Not yet implemented")
    }

    override fun getQuestRewards(gameId: Long): List<InGameQuestReward> {
        TODO("Not yet implemented")
    }

    override fun getUserQuestProgresses(gameId: Long): List<InGameUserQuestProgress> {
        TODO("Not yet implemented")
    }

    override fun getUserStatusHolders(gameId: Long): List<InGameUserStatusHolder> {
        TODO("Not yet implemented")
    }

    override fun getVisibilityMap(gameId: Long): InGameVisibilityMap? {
        TODO("Not yet implemented")
    }

    override fun getEllipses(gameId: Long): List<InGameLevelZoneEllipse> {
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