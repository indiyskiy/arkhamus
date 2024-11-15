package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class ParalyzeAbilityCast(
    private val timeEventHandler: TimeEventHandler,
) : AbilityCast {

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.PARALYSE
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        paralyze(abilityRequestProcessData, globalGameData)
        return true
    }

    override fun cast(
        sourceUser: RedisGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        paralyze(target as RedisGameUser, globalGameData, sourceUser)
        return true
    }

    private fun paralyze(
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ) {
        val currentUser = abilityRequestProcessData.gameUser
        val targetUser = abilityRequestProcessData.target as RedisGameUser

        paralyze(targetUser, globalGameData, currentUser)
    }

    private fun paralyze(
        targetUser: RedisGameUser,
        globalGameData: GlobalGameData,
        currentUser: RedisGameUser?
    ) {
        if (targetUser.stateTags.contains(UserStateTag.INVULNERABILITY.name)) return

        timeEventHandler.createEvent(
            game = globalGameData.game,
            eventType = RedisTimeEventType.ABILITY_STUN,
            sourceObject = currentUser,
            targetObject = targetUser,
            location = Location(targetUser.x, targetUser.y, targetUser.z),
            timeLeft = RedisTimeEventType.ABILITY_STUN.getDefaultTime()
        )
    }
}