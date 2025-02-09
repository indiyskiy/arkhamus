package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithGameTags
import org.springframework.stereotype.Component

@Component
class InGameTagsHandler {
    fun addTag(withTags: WithGameTags, tag: InGameObjectTag) {
        withTags.writeGameTags(withTags.gameTags().plus(tag))
    }

    fun removeTag(withTags: WithGameTags, tag: InGameObjectTag) {
        withTags.writeGameTags(withTags.gameTags().minus(tag))
    }

}