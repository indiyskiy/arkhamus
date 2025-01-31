package com.arkhamusserver.arkhamus.model.ingame.interfaces

interface Interactable : WithPoint {
    fun interactionRadius(): Double
}