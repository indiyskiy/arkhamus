package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.FAKE_VOTE
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class FakeVoteCurseItemCondition() : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == FAKE_VOTE
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: RedisGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        return true
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return true
    }
}