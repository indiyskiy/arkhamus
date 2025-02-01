package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.InGameTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameQuestGiverRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameQuestGiver
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithGameTags
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class DarkTemptationAbilityCast(
    private val inGameTagsHandler: InGameTagsHandler,
    private val inGameQuestGiverRepository: InGameQuestGiverRepository,
) : AbilityCast {
    override fun accept(ability: Ability): Boolean {
        return ability == Ability.DARK_TEMPTATION
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        temptTarget(abilityRequestProcessData)
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        temptTarget(target)
        return true
    }

    private fun temptTarget(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val target = abilityRequestProcessData.target
        temptTarget(target)
    }

    private fun temptTarget(target: WithStringId?) {
        if (target != null) {
            inGameTagsHandler.addTag(target as WithGameTags, InGameObjectTag.DARK_THOUGHTS)
            if (target is InGameQuestGiver) {
                inGameQuestGiverRepository.save(target)
            }
        }
    }

}