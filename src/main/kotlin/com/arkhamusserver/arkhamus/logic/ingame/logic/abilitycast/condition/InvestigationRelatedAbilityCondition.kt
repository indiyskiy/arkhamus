package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.InvestigationRelatedAbilityProcessor
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class InvestigationRelatedAbilityCondition : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability in InvestigationRelatedAbilityProcessor.relatedSet
    }

    override fun fitCondition(
        ability: Ability,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return !user.stateTags.contains(UserStateTag.INVESTIGATING.name)
    }
}