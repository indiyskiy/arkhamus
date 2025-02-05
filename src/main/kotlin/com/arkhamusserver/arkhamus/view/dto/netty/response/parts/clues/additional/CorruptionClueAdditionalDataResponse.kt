package com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional

data class CorruptionClueAdditionalDataResponse (
    var timeUntilFullyGrowth: Long,
    var totalTimeUntilNullify: Long,
    var timeFromStart: Long,
): AdditionalClueDataResponse