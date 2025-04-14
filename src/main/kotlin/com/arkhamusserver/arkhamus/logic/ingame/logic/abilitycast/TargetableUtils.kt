package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component

@Component
class TargetableUtils {

    companion object {
        private val relatedTags = setOf(
            UserStateTag.STEALTH,
            UserStateTag.IN_RITUAL,
        )
    }

    fun isTargetable(
        targetUser: InGameUser
    ): Boolean {
        return targetUser.stateTags.none { it in relatedTags }
    }
}