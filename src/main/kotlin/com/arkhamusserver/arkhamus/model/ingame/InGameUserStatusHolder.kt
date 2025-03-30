package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameUserStatus

data class InGameUserStatusHolder(
    override var id: String,
    override val gameId: Long,
    val userId: Long,
    val status: InGameUserStatus,
    var prolongation: Boolean,
    val started: Long
) : InGameEntity