package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
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
    private val geometryUtils: GeometryUtils,
    private val redisAltarHolderRepository: RedisAltarHolderRepository
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
        val altarHolder = globalGameData.altarHolder
        val gameTimeItemsNotches = ritualHandler.countItemsNotches(event, altarHolder)
        val currentItem = ritualHandler.countCurrentItem(gameTimeItemsNotches, currentGameTime)

        if (altarHolder != null && altarHolder.currentStepItem != currentItem) {
            altarHolder.currentStepItem = currentItem
            if (altarHolder.usersToKick.isNotEmpty()) {
                ritualHandler.kickUsersFromRitual(altarHolder, globalGameData)
            }
            redisAltarHolderRepository.save(altarHolder)
        }

        if (currentItem != null) {
            ritualHandler.tryToShiftTime(globalGameData.altarHolder, currentItem, event)
        }
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
        val altarHolder = globalGameData.altarHolder
        if (altarHolder == null || !altarHolder.thmAddedThisRound) {
            logger.info("RITUAL_GOING process ending")
            ritualHandler.failRitualByTime(altarHolder, globalGameData)
            logger.info("RITUAL_GOING process ended")
        } else {
            logger.info("RITUAL_GOING process ending, but starts another round")
            ritualHandler.startAnotherRound(globalGameData, altarHolder)
        }
    }

    private fun setUsersPosition(
        values: Collection<RedisGameUser>,
        altarHolder: RedisAltarHolder,
        radius: Double
    ) {
        val usersInRitual = values.filter { it.inGameId() in altarHolder.usersInRitual }.sortedBy { it.inGameId() }
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
        users.filterNot { user -> user.inRitual(altarHolder) }.forEach { user ->
            if (geometryUtils.distanceLessOrEquals(
                    altarHolder, user, radius
                )
            ) {
                user.stateTags += IN_RITUAL
                altarHolder.usersInRitual += user.inGameId()
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

    private fun RedisGameUser.inRitual(altarHolder: RedisAltarHolder): Boolean =
        altarHolder.usersInRitual.contains(this.inGameId())

}
