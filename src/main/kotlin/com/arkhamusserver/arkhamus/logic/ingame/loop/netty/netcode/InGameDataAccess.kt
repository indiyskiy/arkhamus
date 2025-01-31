package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.ingame.*
import org.springframework.transaction.annotation.Transactional

interface InGameDataAccess {
    fun getGameUser(userId: Long?, gameId: Long?): InGameGameUser?
    fun getGameUsers(gameId: Long?): List<InGameGameUser>
    fun getGame(gameId: Long): InRamGame?
    fun getContainers(gameId: Long): List<InGameContainer>
    fun getCrafters(gameId: Long): List<InGameCrafter>
    fun getLanterns(gameId: Long): List<InGameLantern>
    fun getVoteSpots(gameId: Long): List<InGameVoteSpot>
    fun getUserVoteSpots(gameId: Long): List<InGameUserVoteSpot>
    fun getThresholds(gameId: Long): List<InGameThreshold>
    fun getDoors(gameId: Long): List<InGameDoor>
    fun getAltarHolder(gameId: Long): InGameAltarHolder?
    fun getAltarPolling(gameId: Long): InGameAltarPolling?
    fun getAltars(gameId: Long): Map<Long, InGameAltar>
    fun getTimeEvents(gameId: Long): List<InGameTimeEvent>
    fun getShortTimeEvents(gameId: Long): List<InGameShortTimeEvent>
    fun getCastAbilities(gameId: Long): List<InGameAbilityCast>
    fun getCraftProcess(gameId: Long): List<InGameCraftProcess>
    fun getZones(gameId: Long): List<InGameLevelZone>
    fun getTetragons(gameId: Long): List<InGameLevelZoneTetragon>
    fun getEllipses(gameId: Long): List<InGameLevelZoneEllipse>
    fun getClues(gameId: Long): CluesContainer
    fun getQuests(gameId: Long): List<InGameQuest>
    fun getQuestGivers(gameId: Long): List<InGameQuestGiver>
    fun getQuestRewards(gameId: Long): List<InGameQuestReward>
    fun getUserQuestProrgesses(gameId: Long): List<InGameUserQuestProgress>
    fun getVisibilityMap(gameId: Long): InGameVisibilityMap?
}

@Transactional(readOnly = true)
fun InGameDataAccess.loadGlobalGameData(game: InRamGame): GlobalGameData {
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

    val visibilityMap = getVisibilityMap(gameId)

    return GlobalGameData(
        game = game,
        altarHolder = altarHolder,
    ).apply {
        this.altars = altars
        this.altarPolling = altarPolling
        this.users = allUsers.associateBy { it.inGameId() }
        this.containers = allContainers.associateBy { it.inGameId() }
        this.crafters = allCrafters.associateBy { it.inGameId() }
        this.timeEvents = allEvents
        this.shortTimeEvents = allShortEvents
        this.castAbilities = castAbilities
        this.craftProcess = craftProcess
        this.lanterns = allLanterns
        this.clues = allClues
        // TODO get rid of these '!!'
        this.levelGeometryData = buildGeometryData(zones, tetragons, ellipses, visibilityMap!!)
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

fun InGameDataAccess.getOtherGameUsers(userId: Long?, gameId: Long?): List<InGameGameUser> =
    getGameUsers(gameId).filter { it.inGameId() != userId }
