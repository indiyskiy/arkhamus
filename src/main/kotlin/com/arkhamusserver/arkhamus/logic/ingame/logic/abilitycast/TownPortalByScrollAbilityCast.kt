package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TeleportHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.WithPoint
import org.springframework.stereotype.Component

@Component
class TownPortalByScrollAbilityCast(
    private val teleportHandler: TeleportHandler,
) : AbilityCast {

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.TOWN_PORTAL_BY_SCROLL
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ) {
        portalToLastInterestPoint(globalGameData, abilityRequestProcessData)
    }

    private fun portalToLastInterestPoint(
        globalGameData: GlobalGameData,
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val user = abilityRequestProcessData.gameUser
        user?.let {
            teleportHandler.forceTeleport(
                game = globalGameData.game,
                user = it,
                point = findLastInterestPoint(globalGameData)
            )
        }
    }

    private fun findLastInterestPoint(data: GlobalGameData): WithPoint? {
        if (data.timeEvents.any {
                (it.type == RedisTimeEventType.ALTAR_VOTING ||
                        it.type == RedisTimeEventType.RITUAL_GOING
                        ) && it.state == RedisTimeEventState.ACTIVE
            }
        ) {
            return data.altarHolder
        }
        val goingBanVoteCall = data.timeEvents.firstOrNull {
            (it.type == RedisTimeEventType.CALL_FOR_BAN_VOTE
                    ) && it.state == RedisTimeEventState.ACTIVE
        }
        if (goingBanVoteCall != null) {
            return Location(
                goingBanVoteCall.xLocation!!,
                goingBanVoteCall.yLocation!!,
                goingBanVoteCall.zLocation!!,
            )
        }
        return data.altarHolder
    }
}