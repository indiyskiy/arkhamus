package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.model.dataaccess.redis.*
import com.arkhamusserver.arkhamus.model.dataaccess.redis.clues.RedisOmenClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.clues.RedisScentClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.clues.RedisSoundClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameRepository
import com.arkhamusserver.arkhamus.model.redis.*
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class RedisDataAccessImpl(
    private val gameUserRepository: RedisGameUserRepository,
    private val gameRepository: RedisGameRepository,
    private val containerRepository: RedisContainerRepository,
    private val crafterRepository: RedisCrafterRepository,
    private val lanternRepository: RedisLanternRepository,
    private val voteSpotRepository: RedisVoteSpotRepository,
    private val userVoteSpotRepository: RedisUserVoteSpotRepository,
    private val thresholdRepository: RedisThresholdRepository,
    private val doorRepository: RedisDoorRepository,
    private val altarRepository: RedisAltarRepository,
    private val altarHolderRepository: RedisAltarHolderRepository,
    private val altarPollingRepository: RedisAltarPollingRepository,
    private val timeEventRepository: RedisTimeEventRepository,
    private val shortTimeEventRepository: RedisShortTimeEventRepository,
    private val abilityCastRepository: RedisAbilityCastRepository,
    private val craftProcessRepository: RedisCraftProcessRepository,
    private val redisLevelZoneRepository: RedisLevelZoneRepository,
    private val redisLevelTetragonRepository: RedisLevelTetragonRepository,
    private val redisLevelEllipseRepository: RedisLevelEllipseRepository,
    private val redisQuestRepository: RedisQuestRepository,
    private val redisQuestGiverRepository: RedisQuestGiverRepository,
    private val redisUserQuestProgressRepository: RedisUserQuestProgressRepository,
    private val redisQuestRewardRepository: RedisQuestRewardRepository,
    private val redisScentClueRepository: RedisScentClueRepository,
    private val redisVisibilityMapRepository: RedisVisibilityMapRepository,
    private val redisSoundClueRepository: RedisSoundClueRepository,
    private val redisOmenClueRepository: RedisOmenClueRepository
) : RedisDataAccess {

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

    override fun getThresholds(gameId: Long): List<RedisThreshold> =
        thresholdRepository.findByGameId(gameId)

    override fun getDoors(gameId: Long): List<RedisDoor> =
        doorRepository.findByGameId(gameId)

    override fun getTimeEvents(gameId: Long) =
        timeEventRepository.findByGameId(gameId)

    override fun getShortTimeEvents(gameId: Long) =
        shortTimeEventRepository.findByGameId(gameId)

    override fun getCastAbilities(gameId: Long): List<RedisAbilityCast> =
        abilityCastRepository.findByGameId(gameId)

    override fun getCraftProcess(gameId: Long): List<RedisCraftProcess> =
        craftProcessRepository.findByGameId(gameId)

    override fun getZones(gameId: Long): List<RedisLevelZone> {
        return redisLevelZoneRepository.findByGameId(gameId)
    }

    override fun getTetragons(gameId: Long): List<RedisLevelZoneTetragon> {
        return redisLevelTetragonRepository.findByGameId(gameId)
    }

    override fun getClues(gameId: Long): CluesContainer {
        val scent = redisScentClueRepository.findByGameId(gameId)
        val sound = redisSoundClueRepository.findByGameId(gameId)
        val omen = redisOmenClueRepository.findByGameId(gameId)
        return CluesContainer(scent, sound, omen)
    }

    override fun getQuests(gameId: Long): List<RedisQuest> {
        return redisQuestRepository.findByGameId(gameId)
    }

    override fun getQuestGivers(gameId: Long): List<RedisQuestGiver> {
        return redisQuestGiverRepository.findByGameId(gameId)
    }

    override fun getQuestRewards(gameId: Long): List<RedisQuestReward> {
        return redisQuestRewardRepository.findByGameId(gameId)
    }

    override fun getUserQuestProrgesses(gameId: Long): List<RedisUserQuestProgress> {
        return redisUserQuestProgressRepository.findByGameId(gameId)
    }

    override fun getEllipses(gameId: Long): List<RedisLevelZoneEllipse> {
        return redisLevelEllipseRepository.findByGameId(gameId)
    }

    override fun getRedisVisibilityMap(gameId: Long): RedisVisibilityMap? {
        return redisVisibilityMapRepository.findByGameId(gameId).firstOrNull()
    }

}