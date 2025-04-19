package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.model.ingame.clues.*

data class CluesContainer(
    var aura: List<InGameAuraClue>,
    var scent: List<InGameScentClue>,
    var sound: List<InGameSoundClue>,
    var omen: List<InGameOmenClue>,
    var corruption: List<InGameCorruptionClue>,
    var distortion: List<InGameDistortionClue>,
    var inscription: List<InGameInscriptionClue>,
)