package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithGameTags
import org.springframework.stereotype.Component

@Component
class InGameTagsHandler {
    fun addTag(withTags: WithGameTags, tag: InGameObjectTag) {
        if(withTags.gameTags().contains(tag)) return
        withTags.rewriteGameTags(withTags.gameTags() + tag)
    }

}