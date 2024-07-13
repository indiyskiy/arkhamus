package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InvestigationRelatedAbilityProcessor : ActiveAbilityProcessor {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(InvestigationRelatedAbilityProcessor::class.java)
        val relatedSet = setOf(
            Ability.SEARCH_FOR_INSCRIPTION,
            Ability.SEARCH_FOR_SOUND,
            Ability.SEARCH_FOR_SCENT,
            Ability.SEARCH_FOR_AURA,
            Ability.SEARCH_FOR_CORRUPTION,
            Ability.SEARCH_FOR_OMEN,
            Ability.SEARCH_FOR_DISTORTION,
        )
        val relatedSetIds = relatedSet.map { it.id }.toSet()
    }

    override fun accepts(castAbility: RedisAbilityCast): Boolean {
        return castAbility.abilityId.toAbility()?.let { ability ->
            ability in relatedSet
        } ?: false
    }

    override fun processActive(
        castAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: RedisAbilityCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castAbility.sourceUserId]
        user?.stateTags?.let { tags ->
            if (!globalGameData.castAbilities.any { ability ->
                    ability.state == RedisTimeEventState.ACTIVE &&
                            ability.sourceUserId == castAbility.sourceUserId &&
                            ability.id != castAbility.id &&
                            ability.abilityId in relatedSetIds
                }
            ) {
                logger.info("remove INVESTIGATING tag from user ${castAbility.sourceUserId}")
                tags.remove(UserStateTag.INVESTIGATING.name)
                logger.info("new user tags list is ${tags.joinToString()}")
            } else {
                logger.info("there is still active investigating source for user ${castAbility.sourceUserId}")
            }
        }
    }

    private fun Int.toAbility(): Ability? =
        Ability.values().firstOrNull { it.id == this }

}


