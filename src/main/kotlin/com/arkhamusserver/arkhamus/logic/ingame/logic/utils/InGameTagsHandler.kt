package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithGameTags
import org.springframework.stereotype.Component

@Component
class InGameTagsHandler {
    fun addTag(withTags: WithGameTags, tag: InGameObjectTag) {
        withTags.gameTags().add(tag.name)
    }

    fun removeTag(withTags: WithGameTags, tag: String) {
        withTags.gameTags().remove(tag)
    }

    fun removeTag(withTags: WithGameTags, tag: InGameObjectTag) {
        withTags.gameTags().remove(tag.name)
    }

}