package com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional

data class SoundClueJammerResponse(
    val id: Long,
    val x: Double,
    val y: Double,
    val z: Double,
    val turnedOn: Boolean
)