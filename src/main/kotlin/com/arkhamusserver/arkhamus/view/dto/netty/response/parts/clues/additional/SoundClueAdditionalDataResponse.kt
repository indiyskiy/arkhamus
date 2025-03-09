package com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional

data class SoundClueAdditionalDataResponse(
    var soundClueJammers: List<SoundClueJammerResponse> = listOf(),
) : AdditionalClueDataResponse