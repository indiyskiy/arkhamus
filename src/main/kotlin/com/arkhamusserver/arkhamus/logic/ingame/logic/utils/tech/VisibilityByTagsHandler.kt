package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import org.springframework.stereotype.Component

@Component
class VisibilityByTagsHandler {
    fun userCanSeeTarget(
        who: WithVisibilityModifiers,
        target: WithVisibilityModifiers
    ): Boolean {
        return who
            .visibilityModifiers().any {
                it in target.visibilityModifiers()
            }
    }
}