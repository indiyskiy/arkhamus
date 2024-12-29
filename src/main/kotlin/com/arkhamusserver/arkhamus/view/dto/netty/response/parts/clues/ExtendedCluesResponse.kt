package com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues

data class ExtendedCluesResponse(
    private val possibleClues: List<ExtendedClueResponse>,
    private val actualClues: List<ExtendedClueResponse>
)