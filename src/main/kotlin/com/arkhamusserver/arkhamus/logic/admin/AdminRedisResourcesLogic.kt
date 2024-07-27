package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.redis.*
import com.arkhamusserver.arkhamus.view.dto.admin.AdminRedisResourcesInfoDto
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceDto
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType.*
import org.springframework.stereotype.Component

@Component
class AdminRedisResourcesLogic(
    private val gameUserRepository: RedisGameUserRepository,
    private val gameRepository: RedisGameRepository,
    private val containerRepository: RedisContainerRepository,
    private val crafterRepository: RedisCrafterRepository,
    private val lanternRepository: RedisLanternRepository,
    private val altarRepository: RedisAltarRepository,
    private val altarHolderRepository: RedisAltarHolderRepository,
    private val altarPollingRepository: RedisAltarPollingRepository,
    private val timeEventRepository: RedisTimeEventRepository,
    private val abilityCastRepository: RedisAbilityCastRepository,
    private val craftProcessRepository: RedisCraftProcessRepository,
    private val redisLevelZoneRepository: RedisLevelZoneRepository,
    private val redisLevelTetragonRepository: RedisLevelTetragonRepository,
    private val redisLevelEllipseRepository: RedisLevelEllipseRepository,
    private val redisClueRepository: RedisClueRepository,
    private val redisQuestRepository: RedisQuestRepository,
    private val redisQuestRewardRepository: RedisQuestRewardRepository
) {
    fun info(): AdminRedisResourcesInfoDto {
        return AdminRedisResourcesInfoDto(
            listOf(
                gameRepository.findAll().mapInfo(GAME),
                gameUserRepository.findAll().mapInfo(GAME_USER),
                containerRepository.findAll().mapInfo(CONTAINER),
                crafterRepository.findAll().mapInfo(CRAFTER),
                lanternRepository.findAll().mapInfo(LANTERN),
                altarRepository.findAll().mapInfo(ALTAR),
                altarHolderRepository.findAll().mapInfo(ALTAR_HOLDER),
                altarPollingRepository.findAll().mapInfo(ALTAR_POLLING),
                timeEventRepository.findAll().mapInfo(TIME_EVENT),
                abilityCastRepository.findAll().mapInfo(ABILITY_CAST),
                craftProcessRepository.findAll().mapInfo(CRAFT_PROCESS),
                redisLevelZoneRepository.findAll().mapInfo(LEVEL_ZONE),
                redisLevelTetragonRepository.findAll().mapInfo(TETRAGON),
                redisLevelEllipseRepository.findAll().mapInfo(ELLIPSE),
                redisClueRepository.findAll().mapInfo(CLUE),
                redisQuestRepository.findAll().mapInfo(QUEST),
                redisQuestRewardRepository.findAll().mapInfo(QUEST_REWARD),
            )
        )
    }

    private fun <T> Iterable<T>.mapInfo(type: RedisResourceType): RedisResourceDto {
        return RedisResourceDto(type, this.toList().size)
    }
}


