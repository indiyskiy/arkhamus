package com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional

data class DistortionClueAdditionalDataResponse(
    private val connected: Boolean,
    private val otherSideId: Long?,
    private val usersInSight: List<SimpleUserAdditionalDataResponse> = emptyList(),
) : AdditionalClueDataResponse