package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.model.ingame.clues.InGameOmenClue
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameScentClue
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameSoundClue

data class CluesContainer(
    var scent: List<InGameScentClue>,
    var sound: List<InGameSoundClue>,
    var omen: List<InGameOmenClue>
)