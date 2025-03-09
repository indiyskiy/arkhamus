package com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional

import com.arkhamusserver.arkhamus.model.ingame.parts.InGameUserSkinSetting

data class SimpleUserAdditionalDataResponse(
    var id: Long? = null,
    var nickName: String? = null,
    var skin: InGameUserSkinSetting? = null,
    var x: Double? = null,
    var y: Double? = null,
    var z: Double? = null,
)