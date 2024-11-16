package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.logic.admin.NiceColor

data class PointDto(
    var pointX: Float, var pointY: Float, var color: NiceColor
)