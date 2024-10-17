package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.InvestigationRelatedAbilityProcessor
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class InvestigationRelatedAbilityCondition : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability in InvestigationRelatedAbilityProcessor.relatedSet
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: RedisGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        return canBeCastedAtAll(
            ability,
            user,
            globalGameData
        )
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return !user.stateTags.contains(UserStateTag.INVESTIGATING.name)
    }
}