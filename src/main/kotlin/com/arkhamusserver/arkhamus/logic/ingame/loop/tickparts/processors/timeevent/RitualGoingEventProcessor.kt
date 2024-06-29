package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.DistanceHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag.IN_RITUAL
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
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
    private val distanceHandler: DistanceHandler
) : TimeEventProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(RitualGoingEventProcessor::class.java)
    }

    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.RITUAL_GOING

    override fun processStart(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        logger.info("RITUAL_GOING process started")
        addUsersToRitual(globalGameData)
    }

    override fun process(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        addUsersToRitual(globalGameData)
    }

    private fun addUsersToRitual(globalGameData: GlobalGameData) {
        val center = countCenter(globalGameData.altars.values)
        val altar = globalGameData.altars.values.first()
        val radius = distance(altar, center) + altar.interactionRadius
        addUsersToRitual(globalGameData.users.values, center, radius)
        setUsersPosition(globalGameData.users.values, center, radius)
    }

    override fun processEnd(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        logger.info("RITUAL_GOING process ending")
        globalGameData.altarPolling?.let {
            ritualHandler.failRitual(
                globalGameData.altarHolder,
                it,
                globalGameData.timeEvents,
                globalGameData.game
            )
        }
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


    private fun setUsersPosition(values: Collection<RedisGameUser>, center: Pair<Double, Double>, radius: Double) {
        val usersInRitual = values.filter { it.stateTags.contains(IN_RITUAL.name) }.sortedBy { it.userId }
        val usersRadius = radius * 2 / 3
        if (usersInRitual.isNotEmpty()) {
            val step = 2 * Math.PI / usersInRitual.size
            usersInRitual.mapIndexed { index, redisGameUser ->
                val x = usersRadius * cos(index * step) + center.first
                val y = usersRadius * sin(index * step) + center.second
                redisGameUser.x = x
                redisGameUser.y = y
            }
        }
    }

    private fun addUsersToRitual(
        users: Collection<RedisGameUser>,
        center: Pair<Double, Double>,
        radius: Double
    ) {
        users.filterNot { user -> user.inRitual() }.forEach { user ->
            if (distanceHandler.distanceLessOrEquals(
                    center.first, center.second, user.x, user.y, radius
                )
            ) {
                user.stateTags.add(IN_RITUAL.name)
            }
        }
    }

    private fun distance(
        altar: RedisAltar,
        center: Pair<Double, Double>
    ): Double {
        return distanceHandler.distance(
            altar.x, altar.y, center.first, center.second
        )
    }

    private fun countCenter(values: Collection<RedisAltar>): Pair<Double, Double> {
        val x = values.sumOf { it.x } / values.size
        val y = values.sumOf { it.y } / values.size
        return Pair(x, y)
    }

    private fun RedisGameUser.inRitual(): Boolean =
        this.stateTags.contains(IN_RITUAL.name)

}
