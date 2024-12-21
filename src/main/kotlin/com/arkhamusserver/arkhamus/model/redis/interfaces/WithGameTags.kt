package com.arkhamusserver.arkhamus.model.redis.interfaces

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag

interface WithGameTags {
    fun gameTags(): Set<InGameObjectTag>
    fun writeGameTags(gameTags: Set<InGameObjectTag>)
}