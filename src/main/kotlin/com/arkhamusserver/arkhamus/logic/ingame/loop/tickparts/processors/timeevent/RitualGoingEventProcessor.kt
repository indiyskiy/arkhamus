package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag.IN_RITUAL
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.cos
import kotlin.math.sin

@Component
class RitualGoingEventProcessor(
    private val ritualHandler: RitualHandler,
    private val geometryUtils: GeometryUtils
) : TimeEventProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(RitualGoingEventProcessor::class.java)
    }

    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.RITUAL_GOING

    override fun processStart(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        logger.info("RITUAL_GOING process started")
        addUsersToRitual(globalGameData)
    }

    override fun process(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        addUsersToRitual(globalGameData)
    }

    private fun addUsersToRitual(globalGameData: GlobalGameData) {
        globalGameData.altarHolder?.let {
            val altar = globalGameData.altars.values.first()
            val radius = distance(altar, it) + altar.interactionRadius
            addUsersToRitual(globalGameData.users.values, it, radius)
            setUsersPosition(globalGameData.users.values, it, radius)
        }
    }

    override fun processEnd(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        logger.info("RITUAL_GOING process ending")
        ritualHandler.failRitualStartCooldown(
            globalGameData.altarHolder,
            globalGameData.altarPolling,
            globalGameData.timeEvents,
            globalGameData.game
        )
        logger.info("ritual failed")
        globalGameData
            .users
            .values
            .filter { user ->
                user.stateTags.contains(IN_RITUAL.name)
            }
            .forEach { user ->
                user.stateTags.remove(IN_RITUAL.name)
            }
        logger.info("users from ritual removed")
        logger.info("RITUAL_GOING process ended")
    }


    private fun setUsersPosition(
        values: Collection<RedisGameUser>,
        altarHolder: RedisAltarHolder,
        radius: Double
    ) {
        val usersInRitual = values.filter { it.stateTags.contains(IN_RITUAL.name) }.sortedBy { it.userId }
        val usersRadius = radius * 2 / 3
        if (usersInRitual.isNotEmpty()) {
            val step = 2 * Math.PI / usersInRitual.size
            usersInRitual.mapIndexed { index, redisGameUser ->
                val x = usersRadius * cos(index * step) + altarHolder.x
                val y = altarHolder.y
                val z = usersRadius * sin(index * step) + altarHolder.z
                redisGameUser.x = x
                redisGameUser.y = y
                redisGameUser.z = z
            }
        }
    }

    private fun addUsersToRitual(
        users: Collection<RedisGameUser>,
        altarHolder: RedisAltarHolder,
        radius: Double
    ) {
        users.filterNot { user -> user.inRitual() }.forEach { user ->
            if (geometryUtils.distanceLessOrEquals(
                    altarHolder, user, radius
                )
            ) {
                user.stateTags.add(IN_RITUAL.name)
            }
        }
    }

    private fun distance(
        altar: RedisAltar,
        altarHolder: RedisAltarHolder
    ): Double {
        return geometryUtils.distance(
            altar, altarHolder
        )
    }

    private fun RedisGameUser.inRitual(): Boolean =
        this.stateTags.contains(IN_RITUAL.name)

}
