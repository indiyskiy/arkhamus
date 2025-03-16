package com.arkhamusserver.arkhamus.model.ingame.parts

data class TechInGameUserData(
    var won: Boolean? = null,
    var sawTheEndOfTimes: Boolean = false,
    var connected: Boolean,
    var leftTheGame: Boolean = false,
)