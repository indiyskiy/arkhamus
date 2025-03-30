package com.arkhamusserver.arkhamus.model.ingame.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuff

data class MadnessAdditionalInGameUserData(
    var madness: Double,
    var madnessNotches: List<Double>,
    var madnessDebuffs: Set<MadnessDebuff> = emptySet(),
)