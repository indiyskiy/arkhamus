package com.arkhamusserver.arkhamus.model.enums.ingame.core

fun Int.toClassInGame(): ClassInGame? {
    val id = this
    return ClassInGame.values().firstOrNull { it.id == id }
}