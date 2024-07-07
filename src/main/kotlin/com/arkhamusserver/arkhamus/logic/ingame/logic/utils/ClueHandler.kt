package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.AbilityToGodTypeResolver
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import org.springframework.stereotype.Component

@Component
class ClueHandler(
    private val abilityToGodTypeResolver: AbilityToGodTypeResolver,
    private val abilityHandler: AbilityHandler
) {
    fun filterClues(
        clues: List<RedisClue>,
        inZones: List<LevelZone>,
        castedAbilities: List<RedisAbilityCast>,
        userId: Long
    ): List<RedisClue> {
        val myOngoingAbilities = abilityHandler.myActiveAbilities(userId, castedAbilities)
        val possibleClues = myOngoingAbilities
            .mapNotNull {
                it.abilityId.toAbility()
            }.map {
                abilityToGodTypeResolver.resolve(it)
            }.toSet()

        val zonesSet = inZones.filter {
            it.zoneType == ZoneType.CLUE
        }.map {
            it.zoneId
        }.toSet()
        return clues.filter { it.clue in possibleClues && it.levelZoneId in zonesSet }
    }

    private fun Int.toAbility(): Ability? {
        return Ability.values().firstOrNull { it.id == this }
    }
}