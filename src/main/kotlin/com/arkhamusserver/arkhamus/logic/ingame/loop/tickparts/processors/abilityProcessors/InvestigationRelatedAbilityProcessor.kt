package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.springframework.stereotype.Component

@Component
class InvestigationRelatedAbilityProcessor() : ActiveAbilityProcessor {
    override fun accepts(castedAbility: RedisAbilityCast): Boolean {
        return castedAbility.abilityId.toAbility()?.let { ability ->
            ability in setOf(
                Ability.SEARCH_FOR_INSCRIPTION,
                Ability.SEARCH_FOR_SOUND,
                Ability.SEARCH_FOR_SCENT,
                Ability.SEARCH_FOR_AURA,
                Ability.SEARCH_FOR_CORRUPTION,
                Ability.SEARCH_FOR_OMEN,
                Ability.SEARCH_FOR_DISTORTION,
            )
        } ?: false
    }

    override fun processActive(
        castedAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {
        val user = globalGameData.users[castedAbility.sourceUserId]
        user?.stateTags?.add(UserStateTag.INVESTIGATING.name)
    }

    override fun finishActive(castedAbility: RedisAbilityCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castedAbility.sourceUserId]
        user?.stateTags?.remove(UserStateTag.INVESTIGATING.name)
    }

    private fun Int.toAbility(): Ability? =
        Ability.values().firstOrNull { it.id == this }

}


