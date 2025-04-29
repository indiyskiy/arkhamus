package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.cultist

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.InGameTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityActiveCast
import com.arkhamusserver.arkhamus.model.ingame.InGameQuestGiver
import org.springframework.stereotype.Component

@Component
class DarkTemptationAbilityProcessor(
    val finder: GameObjectFinder,
    val tagsHandler: InGameTagsHandler
) : ActiveAbilityProcessor {

    override fun accepts(castAbility: InGameAbilityActiveCast): Boolean {
        return castAbility.ability == Ability.DARK_TEMPTATION
    }

    override fun processActive(
        castAbility: InGameAbilityActiveCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(
        castAbility: InGameAbilityActiveCast,
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


