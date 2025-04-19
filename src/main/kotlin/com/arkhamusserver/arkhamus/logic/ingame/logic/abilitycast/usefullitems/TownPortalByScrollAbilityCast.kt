package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.usefullitems

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TeleportHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TownPortalByScrollAbilityCast(
    private val teleportHandler: TeleportHandler,
) : AbilityCast {

    companion object {
        private val logger = LoggingUtils.getLogger<TownPortalByScrollAbilityCast>()
    }

    override fun accept(ability: Ability): Boolean {
        return ability in setOf(Ability.TOWN_PORTAL_BY_SCROLL, Ability.TOWN_PORTAL_BY_AMULET)
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        portalToLastInterestPoint(globalGameData, abilityRequestProcessData)
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        castTownPortalAbility(globalGameData, sourceUser)
        return true
    }

    private fun portalToLastInterestPoint(
        globalGameData: GlobalGameData,
        abilityRequestProcessData: AbilityRequestProcessData,
    ) =
        abilityRequestProcessData.gameUser?.let { userNotNull ->
            castTownPortalAbility(globalGameData, userNotNull)
        }

    private fun castTownPortalAbility(
        globalGameData: GlobalGameData,
        userNotNull: InGameUser
    ) {
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

        if (goingBanVoteCall != null) {
            logger.info("teleport user to goingBanVoteCall")
            return Location(
                goingBanVoteCall.xLocation!!,
                goingBanVoteCall.yLocation!!,
                goingBanVoteCall.zLocation!!,
            )
        }
        val goingBanVoteCallFake = findCallForBanFake(data)
        if (goingBanVoteCallFake != null) {
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

    private fun findCallForBan(data: GlobalGameData): InGameTimeEvent? = data.timeEvents.firstOrNull {
        (it.type == InGameTimeEventType.CALL_FOR_BAN_VOTE
                ) && it.state == InGameTimeEventState.ACTIVE
    }

    private fun findCallForBanFake(data: GlobalGameData): InGameTimeEvent? = data.timeEvents.firstOrNull {
        (it.type == InGameTimeEventType.FAKE_CALL_FOR_BAN_VOTE
                ) && it.state == InGameTimeEventState.ACTIVE
    }

    private fun ritualGoing(data: GlobalGameData): Boolean = data.timeEvents.any {
        (it.type == InGameTimeEventType.ALTAR_VOTING ||
                it.type == InGameTimeEventType.RITUAL_GOING
                ) && it.state == InGameTimeEventState.ACTIVE
    }

    private fun fakeRitualGoing(data: GlobalGameData): Boolean = data.timeEvents.any {
        (it.type == InGameTimeEventType.FAKE_ALTAR_VOTING
                ) && it.state == InGameTimeEventState.ACTIVE
    }
}