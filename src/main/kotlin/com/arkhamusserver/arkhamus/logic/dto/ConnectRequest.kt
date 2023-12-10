package com.arkhamusserver.arkhamus.logic.dto

import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession

data class ConnectRequest(
    val player: UserOfGameSession? = null,
    val gameId: Long
)