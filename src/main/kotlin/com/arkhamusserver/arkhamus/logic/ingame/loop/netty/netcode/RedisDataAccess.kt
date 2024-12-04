package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.redis.*
import org.springframework.transaction.annotation.Transactional

interface RedisDataAccess {
    fun getGameUser(userId: Long?, gameId: Long?): RedisGameUser?
    fun getGameUsers(gameId: Long?): List<RedisGameUser>
    fun getGame(gameId: Long): RedisGame?
    fun getContainers(gameId: Long): List<RedisContainer>
    fun getCrafters(gameId: Long): List<RedisCrafter>
    fun getLanterns(gameId: Long): List<RedisLantern>
    fun getVoteSpots(gameId: Long): List<RedisVoteSpot>
    fun getUserVoteSpots(gameId: Long): List<RedisUserVoteSpot>
    fun getThresholds(gameId: Long): List<RedisThreshold>
    fun getDoors(gameId: Long): List<RedisDoor>
    fun getAltarHolder(gameId: Long): RedisAltarHolder?
    fun getAltarPolling(gameId: Long): RedisAltarPolling?
    fun getAltars(gameId: Long): Map<Long, RedisAltar>
    fun getTimeEvents(gameId: Long): List<RedisTimeEvent>
    fun getShortTimeEvents(gameId: Long): List<RedisShortTimeEvent>
    fun getCastAbilities(gameId: Long): List<RedisAbilityCast>
    fun getCraftProcess(gameId: Long): List<RedisCraftProcess>
    fun getZones(gameId: Long): List<RedisLevelZone>
    fun getTetragons(gameId: Long): List<RedisLevelZoneTetragon>
    fun getEllipses(gameId: Long): List<RedisLevelZoneEllipse>
    fun getClues(gameId: Long): List<RedisClue>
    fun getQuests(gameId: Long): List<RedisQuest>
    fun getQuestGivers(gameId: Long): List<RedisQuestGiver>
    fun getQuestRewards(gameId: Long): List<RedisQuestReward>
    fun getUserQuestProrgesses(gameId: Long): List<RedisUserQuestProgress>
}

@Transactional(readOnly = true)
fun RedisDataAccess.loadGlobalGameData(game: RedisGame): GlobalGameData {
    val gameId = game.gameId!!
    val allUsers = getGameUsers(gameId)
    val allContainers = getContainers(gameId)
    val allCrafters = getCrafters(gameId)
    val allEvents = getTimeEvents(gameId)
    val allShortEvents = getShortTimeEvents(gameId)
    val castAbilities = getCastAbilities(gameId)
    val allLanterns = getLanterns(gameId)

    val allVoteSpots = getVoteSpots(gameId)
    val allUserVoteSpots = getUserVoteSpots(gameId)
    val thresholds = getThresholds(gameId)
    val doors = getDoors(gameId)

    val craftProcess = getCraftProcess(gameId)

    val altars = getAltars(gameId)
    val altarHolder = getAltarHolder(gameId)
    val altarPolling = getAltarPolling(gameId)

    val zones = getZones(gameId)
    val tetragons = getTetragons(gameId)
    val ellipses = getEllipses(gameId)

    val allClues = getClues(gameId)

    val allQuests = getQuests(gameId)
    val allQuestGivers = getQuestGivers(gameId)
    val allQuestRewards = getQuestRewards(gameId)
    val allUsersQuestProgresses = getUserQuestProrgesses(gameId)

    return GlobalGameData(
        game = game,
        altarHolder = altarHolder,
    ).apply {
        this.altars = altars
        this.altarPolling = altarPolling
        this.users = allUsers.associateBy { it.userId }
        this.containers = allContainers.associateBy { it.inGameId() }
        this.crafters = allCrafters.associateBy { it.inGameId() }
        this.timeEvents = allEvents
        this.shortTimeEvents = allShortEvents
        this.castAbilities = castAbilities
        this.craftProcess = craftProcess
        this.lanterns = allLanterns
        this.clues = allClues
        this.levelGeometryData = buildGeometryData(zones, tetragons, ellipses)
        this.quests = allQuests
        this.questGivers = allQuestGivers
        this.questRewardsByQuestProgressId = allQuestRewards.groupBy { it.questProgressId }
        this.questProgressByUserId = allUsersQuestProgresses.groupBy { it.userId }
        this.voteSpots = allVoteSpots
        this.userVoteSpotsBySpotId = allUserVoteSpots.groupBy { it.voteSpotId }
        this.thresholds = thresholds
        this.doorsByZoneId = doors.groupBy { it.zoneId }
    }
}

fun RedisDataAccess.getOtherGameUsers(userId: Long?, gameId: Long?): List<RedisGameUser> =
    getGameUsers(gameId).filter { it.userId != userId }

fun RedisDataAccess.getOtherGameUsers(userId: String, gameId: Long) =
    getGameUsers(gameId).filter { it.id != userId }