package com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional

data class InscriptionClueAdditionalDataResponse(
    var possiblyGlyphs: List<PossibleGlyphResponse> = emptyList(),
    var rightGlyph: RightGlyphResponse? = null
) : AdditionalClueDataResponse