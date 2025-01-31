package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.InGameTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithGameTags
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class PeekabooCurseItemAbilityCast(
    private val inGameTagsHandler: InGameTagsHandler,
    private val inGameContainerRepository: InGameContainerRepository,
    private val inGameCrafterRepository: InGameCrafterRepository,
) : AbilityCast {
    override fun accept(ability: Ability): Boolean {
        return ability == Ability.PEEKABOO_CURSE_ITEM
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        curseItem(abilityRequestProcessData)
        return true
    }

    override fun cast(
        sourceUser: InGameGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        curseItem(target)
        return true
    }

    private fun curseItem(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val target = abilityRequestProcessData.target
        curseItem(target)
    }

    private fun curseItem(target: WithStringId?) {
        if (target != null) {
            inGameTagsHandler.addTag(target as WithGameTags, InGameObjectTag.PEEKABOO_CURSE)
            if (target is InGameContainer) {
                inGameContainerRepository.save(target)
            }
            if (target is InGameCrafter) {
                inGameCrafterRepository.save(target)
            }
        }
    }

}