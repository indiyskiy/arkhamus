package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TeleportHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TownPortalByScrollAbilityCast(
    private val teleportHandler: TeleportHandler,
) : AbilityCast {

    companion object {
        private val logger = LoggerFactory.getLogger(TownPortalByScrollAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.TOWN_PORTAL_BY_SCROLL
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        portalToLastInterestPoint(globalGameData, abilityRequestProcessData)
        return true
    }

    private fun portalToLastInterestPoint(
        globalGameData: GlobalGameData,
        abilityRequestProcessData: AbilityRequestProcessData,
    ) =
        abilityRequestProcessData.gameUser?.let { userNotNull ->
            val point = findLastInterestPoint(globalGameData)
            logger.info("teleport user to ${point.x()}; ${point.y()}; ${point.z()}")
            teleportHandler.forceTeleport(
                game = globalGameData.game,
                user = userNotNull,
                point = point
            )
        }

    private fun findLastInterestPoint(data: GlobalGameData): WithPoint {
        if (ritualGoing(data) || fakeRitualGoing(data)) {
            logger.info("teleport user to altarHolder")
            return data.altarHolder!!
        }
        val goingBanVoteCall = findCallForBan(data)

        if (goingBanVoteCall != null ) {
            logger.info("teleport user to goingBanVoteCall")
            return Location(
                goingBanVoteCall.xLocation!!,
                goingBanVoteCall.yLocation!!,
                goingBanVoteCall.zLocation!!,
            )
        }
        val goingBanVoteCallFake = findCallForBanFake(data)
        if (goingBanVoteCallFake != null ) {
            logger.info("teleport user to fake goingBanVoteCall")
            return Location(
                goingBanVoteCallFake.xLocation!!,
                goingBanVoteCallFake.yLocation!!,
                goingBanVoteCallFake.zLocation!!,
            )
        }
        logger.info("teleport user to altarHolder - 2")
        return data.altarHolder!!
    }

    private fun findCallForBan(data: GlobalGameData): RedisTimeEvent? = data.timeEvents.firstOrNull {
        (it.type == RedisTimeEventType.CALL_FOR_BAN_VOTE
                ) && it.state == RedisTimeEventState.ACTIVE
    }

    private fun findCallForBanFake(data: GlobalGameData): RedisTimeEvent? = data.timeEvents.firstOrNull {
        (it.type == RedisTimeEventType.FAKE_CALL_FOR_BAN_VOTE
                ) && it.state == RedisTimeEventState.ACTIVE
    }

    private fun ritualGoing(data: GlobalGameData): Boolean = data.timeEvents.any {
        (it.type == RedisTimeEventType.ALTAR_VOTING ||
                it.type == RedisTimeEventType.RITUAL_GOING
                ) && it.state == RedisTimeEventState.ACTIVE
    }

    private fun fakeRitualGoing(data: GlobalGameData): Boolean = data.timeEvents.any {
        (it.type == RedisTimeEventType.FAKE_ALTAR_VOTING
                ) && it.state == RedisTimeEventState.ACTIVE
    }
}