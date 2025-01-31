package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.InGameTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toAbility
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import com.arkhamusserver.arkhamus.model.ingame.InGameQuestGiver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DarkTemptationAbilityProcessor(
    val finder: GameObjectFinder,
    val tagsHandler: InGameTagsHandler
) : ActiveAbilityProcessor {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DarkTemptationAbilityProcessor::class.java)
    }

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.abilityId.toAbility()?.let { ability ->
            ability == Ability.DARK_TEMPTATION
        } == true
    }

    override fun processActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {
        val targetId = castAbility.targetId
        val targetType = castAbility.targetType
        if (targetId == null || targetType == null) return
        val target = finder.findById(targetId, targetType, globalGameData)
        if (target == null) return
        if (target is InGameQuestGiver) {
            tagsHandler.removeTag(target, InGameObjectTag.DARK_THOUGHTS)
        }
    }

}


