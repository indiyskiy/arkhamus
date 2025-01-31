package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.logic.ingame.logic.visibility.VisibilityMap
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity

class InGameVisibilityMap(
    override var id: String,
    override var gameId: Long,
    // TODO maybe get dir of this composition later
    val visibilityMap: VisibilityMap
) : InGameEntity