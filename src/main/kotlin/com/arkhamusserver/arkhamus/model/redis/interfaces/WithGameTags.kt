package com.arkhamusserver.arkhamus.model.redis.interfaces

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag

interface WithGameTags {
    fun gameTags(): List<InGameObjectTag>
    fun rewriteGameTags(tags: List<InGameObjectTag>)
}