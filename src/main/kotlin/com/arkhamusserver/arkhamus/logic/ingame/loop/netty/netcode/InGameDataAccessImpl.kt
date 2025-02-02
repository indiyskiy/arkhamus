package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.*
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameCorruptionClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameOmenClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameScentClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameSoundClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InRamGameRepository
import com.arkhamusserver.arkhamus.model.ingame.*
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class InGameDataAccessImpl(
    private val gameUserRepository: InGameGameUserRepository,
    private val gameRepository: InRamGameRepository,
    private val containerRepository: InGameContainerRepository,
    private val crafterRepository: InGameCrafterRepository,
    private val lanternRepository: InGameLanternRepository,
    private val voteSpotRepository: InGameVoteSpotRepository,
    private val userVoteSpotRepository: InGameUserVoteSpotRepository,
    private val thresholdRepository: InGameThresholdRepository,
    private val doorRepository: InGameDoorRepository,
    private val altarRepository: InGameAltarRepository,
    private val altarHolderRepository: InGameAltarHolderRepository,
    private val altarPollingRepository: InGameAltarPollingRepository,
    private val timeEventRepository: InGameTimeEventRepository,
    private val shortTimeEventRepository: InGameShortTimeEventRepository,
    private val abilityCastRepository: InGameAbilityCastRepository,
    private val craftProcessRepository: InGameCraftProcessRepository,
    private val inGameLevelZoneRepository: InGameLevelZoneRepository,
    private val inGameLevelTetragonRepository: InGameLevelTetragonRepository,
    private val inGameLevelEllipseRepository: InGameLevelEllipseRepository,
    private val inGameQuestRepository: InGameQuestRepository,
    private val inGameQuestGiverRepository: InGameQuestGiverRepository,
    private val inGameUserQuestProgressRepository: InGameUserQuestProgressRepository,
    private val inGameQuestRewardRepository: InGameQuestRewardRepository,
    private val inGameScentClueRepository: InGameScentClueRepository,
    private val inGameVisibilityMapRepository: InGameVisibilityMapRepository,
    private val inGameSoundClueRepository: InGameSoundClueRepository,
    private val inGameOmenClueRepository: InGameOmenClueRepository,
    private val inGameCorruptionClueRepository: InGameCorruptionClueRepository
) : InGameDataAccess {

    override fun getGameUser(userId: Long?, gameId: Long?) =
        gameUserRepository.findByUserIdAndGameId(userId!!, gameId!!).firstOrNull()

    override fun getGameUsers(gameId: Long?) =
        gameUserRepository.findByGameId(gameId!!)

    override fun getGame(gameId: Long) = gameRepository.findById(gameId.toString()).getOrNull()

    override fun getAltarHolder(gameId: Long) =
        altarHolderRepository.findByGameId(gameId).firstOrNull()

    override fun getAltarPolling(gameId: Long) =
        altarPollingRepository.findByGameId(gameId).maxByOrNull { it.started }

    override fun getAltars(gameId: Long) =
        altarRepository.findByGameId(gameId).associateBy { it.inGameId() }

    override fun getContainers(gameId: Long) =
        containerRepository.findByGameId(gameId)

    override fun getCrafters(gameId: Long) =
        crafterRepository.findByGameId(gameId)

    override fun getLanterns(gameId: Long) =
        lanternRepository.findByGameId(gameId)

    override fun getVoteSpots(gameId: Long) =
        voteSpotRepository.findByGameId(gameId)

    override fun getUserVoteSpots(gameId: Long) =
        userVoteSpotRepository.findByGameId(gameId)

    override fun getThresholds(gameId: Long): List<InGameThreshold> =
        thresholdRepository.findByGameId(gameId)

    override fun getDoors(gameId: Long): List<InGameDoor> =
        doorRepository.findByGameId(gameId)

    override fun getTimeEvents(gameId: Long) =
        timeEventRepository.findByGameId(gameId)

    override fun getShortTimeEvents(gameId: Long) =
        shortTimeEventRepository.findByGameId(gameId)

    override fun getCastAbilities(gameId: Long): List<InGameAbilityCast> =
        abilityCastRepository.findByGameId(gameId)

    override fun getCraftProcess(gameId: Long): List<InGameCraftProcess> =
        craftProcessRepository.findByGameId(gameId)

    override fun getZones(gameId: Long): List<InGameLevelZone> {
        return inGameLevelZoneRepository.findByGameId(gameId)
    }

    override fun getTetragons(gameId: Long): List<InGameLevelZoneTetragon> {
        return inGameLevelTetragonRepository.findByGameId(gameId)
    }

    override fun getClues(gameId: Long): CluesContainer {
        val scent = inGameScentClueRepository.findByGameId(gameId)
        val sound = inGameSoundClueRepository.findByGameId(gameId)
        val omen = inGameOmenClueRepository.findByGameId(gameId)
        val corruption = inGameCorruptionClueRepository.findByGameId(gameId)
        return CluesContainer(scent, sound, omen, corruption)
    }

    override fun getQuests(gameId: Long): List<InGameQuest> {
        return inGameQuestRepository.findByGameId(gameId)
    }

    override fun getQuestGivers(gameId: Long): List<InGameQuestGiver> {
        return inGameQuestGiverRepository.findByGameId(gameId)
    }

    override fun getQuestRewards(gameId: Long): List<InGameQuestReward> {
        return inGameQuestRewardRepository.findByGameId(gameId)
    }

    override fun getUserQuestProrgesses(gameId: Long): List<InGameUserQuestProgress> {
        return inGameUserQuestProgressRepository.findByGameId(gameId)
    }

    override fun getEllipses(gameId: Long): List<InGameLevelZoneEllipse> {
        return inGameLevelEllipseRepository.findByGameId(gameId)
    }

    override fun getVisibilityMap(gameId: Long): InGameVisibilityMap? {
        return inGameVisibilityMapRepository.findByGameId(gameId).firstOrNull()
    }

}