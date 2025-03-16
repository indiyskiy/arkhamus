package com.arkhamusserver.arkhamus.model.ingame.parts


data class MadnessAdditionalInGameUserData(
    var madness: Double,
    var madnessNotches: List<Double>,
    var madnessDebuffs: Set<String> = emptySet(),
)