package com.arkhamusserver.arkhamus.view.dto.user

import com.arkhamusserver.arkhamus.config.UserState
import com.arkhamusserver.arkhamus.model.enums.steam.SteamPersonaState

data class SteamUserShortDto(
    var steamId: String? = null,
    var steamState: SteamPersonaState? = null,
    var nickName: String? = null,
    var userId: Long? = null,
    var state: UserState? = null,
    var lastActive: Long? = null,
)