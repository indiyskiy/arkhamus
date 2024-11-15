package com.arkhamusserver.arkhamus.model.redis.interfaces

interface Interactable : WithPoint {
    fun interactionRadius(): Double
}