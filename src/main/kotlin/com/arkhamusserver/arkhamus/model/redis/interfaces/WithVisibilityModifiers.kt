package com.arkhamusserver.arkhamus.model.redis.interfaces

interface WithVisibilityModifiers {
    fun visibilityModifiers(): MutableSet<String>
}