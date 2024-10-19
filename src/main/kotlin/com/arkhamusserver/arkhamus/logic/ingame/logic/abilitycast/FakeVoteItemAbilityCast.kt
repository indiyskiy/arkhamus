package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisVoteSpot
import org.springframework.stereotype.Component

@Component
class FakeVoteItemAbilityCast(
    private val timeEventHandler: TimeEventHandler
) : AbilityCast {
    override fun accept(ability: Ability): Boolean {
        return ability == Ability.FAKE_VOTE
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        curseItem(
            globalGameData.game,
            abilityRequestProcessData.gameUser!!,
            abilityRequestProcessData,
            globalGameData
        )
        return true
    }

    private fun curseItem(
        game: RedisGame,
        sourceUser: RedisGameUser,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ) {
        val target = abilityRequestProcessData.target
        if (target != null) {
            if (target is RedisAltar) {
                timeEventHandler.createEvent(
                    game,
                    RedisTimeEventType.FAKE_ALTAR_VOTING,
                    sourceUser
                )
            }
            if (target is RedisVoteSpot) {
                val threshold = (globalGameData.thresholdsByZoneId[target.zoneId]?.firstOrNull())?.let {
                    Location(
                        it.x,
                        it.y,
                        it.z
                    )
                }
                timeEventHandler.createEvent(
                    game = game,
                    eventType = RedisTimeEventType.FAKE_CALL_FOR_BAN_VOTE,
                    sourceObject = sourceUser,
                    targetObject = target,
                    location = threshold,
                    timeLeft = RedisTimeEventType.CALL_FOR_BAN_VOTE.getDefaultTime()
                )
            }
        }
    }

}