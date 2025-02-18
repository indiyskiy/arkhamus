package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.model.ingame.clues.InGameAuraClue
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameCorruptionClue
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameDistortionClue
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameOmenClue
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameScentClue
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameSoundClue

data class CluesContainer(
    var aura: List<InGameAuraClue>,
    var scent: List<InGameScentClue>,
    var sound: List<InGameSoundClue>,
    var omen: List<InGameOmenClue>,
    var corruption: List<InGameCorruptionClue>,
    var distortion: List<InGameDistortionClue>,
)