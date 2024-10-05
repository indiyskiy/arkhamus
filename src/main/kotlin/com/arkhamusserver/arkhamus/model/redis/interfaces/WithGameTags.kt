package com.arkhamusserver.arkhamus.model.redis.interfaces

interface WithGameTags {
    fun gameTags(): MutableSet<String>
}