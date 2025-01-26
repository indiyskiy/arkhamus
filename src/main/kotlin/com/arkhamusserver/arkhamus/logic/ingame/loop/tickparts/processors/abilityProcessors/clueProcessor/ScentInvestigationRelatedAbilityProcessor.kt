package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.clueProcessor

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.clues.RedisScentClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toAbility
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ScentInvestigationRelatedAbilityProcessor(
    private val redisScentClueRepository: RedisScentClueRepository
) : ActiveAbilityProcessor {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ScentInvestigationRelatedAbilityProcessor::class.java)
    }

    override fun accepts(castAbility: RedisAbilityCast): Boolean {
        return castAbility.abilityId.toAbility()?.let { ability ->
            ability == Ability.ADVANCED_SEARCH_FOR_SCENT
        } == true
    }

    override fun processActive(
        castAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {
        val scent = globalGameData.clues.scent.first { it.stringId() == castAbility.targetId }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            scent.castedAbilityUsers += user.inGameId()
            redisScentClueRepository.save(scent)
        }
    }

    override fun finishActive(castAbility: RedisAbilityCast, globalGameData: GlobalGameData) {
        val scent = globalGameData.clues.scent.first { it.stringId() == castAbility.targetId }
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.let {
            scent.castedAbilityUsers -= user.inGameId()
            redisScentClueRepository.save(scent)
        }
    }
}