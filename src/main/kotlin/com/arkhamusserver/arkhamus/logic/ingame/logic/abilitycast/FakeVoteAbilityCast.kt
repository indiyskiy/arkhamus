package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.ThresholdType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameAltar
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameVoteSpot
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class FakeVoteAbilityCast(
    private val timeEventHandler: TimeEventHandler
) : AbilityCast {
    companion object {
        private val random = Random(System.currentTimeMillis())
    }

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
            globalGameData
        )
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        curseItem(globalGameData.game, sourceUser, globalGameData)
        return true
    }

    private fun curseItem(
        game: InRamGame,
        sourceUser: InGameUser,
        globalGameData: GlobalGameData
    ) {
        val target = (globalGameData.voteSpots + globalGameData.altars.random(random)).random(random)
        if (target is InGameAltar) {
            timeEventHandler.createEvent(
                game,
                InGameTimeEventType.FAKE_ALTAR_VOTING,
                sourceUser
            )
        }
        if (target is InGameVoteSpot) {
            val threshold = (globalGameData.thresholds.firstOrNull {
                it.zoneId == target.zoneId && it.type == ThresholdType.BAN
            })?.let {
                Location(
                    it.x,
                    it.y,
                    it.z
                )
            }
            timeEventHandler.createEvent(
                game = game,
                eventType = InGameTimeEventType.FAKE_CALL_FOR_BAN_VOTE,
                sourceObject = sourceUser,
                targetObject = target,
                location = threshold,
                timeLeft = InGameTimeEventType.CALL_FOR_BAN_VOTE.getDefaultTime()
            )
        }
    }

}