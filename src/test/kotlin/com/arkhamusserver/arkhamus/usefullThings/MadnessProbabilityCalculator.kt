package com.arkhamusserver.arkhamus.usefullThings

import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random

@SpringBootTest
class MadnessProbabilityCalculator {

    companion object {
        private val logger = LoggingUtils.getLogger<MadnessProbabilityCalculator>()
    }

    private val random: Random = Random(System.currentTimeMillis())

    @Test
    fun test() {
        val probabilityModifier = 120000

        val numberOfMinutes = 600
        val numberOfMillis = numberOfMinutes * 60 * 1000
        val minTickDeltaMs = 100
        val maxTickDeltaMs = 500

        var activations = 0
        var timePast = 0

        while (timePast < numberOfMillis) {
            val currentTickDelta = random.nextInt(minTickDeltaMs, maxTickDeltaMs)
            timePast += currentTickDelta

            val activeValue = probabilityModifier / currentTickDelta
            val active = 0 == random.nextInt(activeValue)
            if (active) activations++
        }
        logger.info("activations = $activations")
        logger.info("activations per minute = ${1.0 * activations / numberOfMinutes}")
    }
}