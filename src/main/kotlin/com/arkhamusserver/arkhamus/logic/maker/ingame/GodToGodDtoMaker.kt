package com.arkhamusserver.arkhamus.logic.maker.ingame

import com.arkhamusserver.arkhamus.logic.dto.ingame.GodDto
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import org.springframework.stereotype.Component

@Component
class GodToGodDtoMaker {
    fun convert(from: Collection<God>): List<GodDto> =
        from.map { god -> convert(god) }

    fun convert(from: God): GodDto =
        GodDto().apply {
            this.name = from.name
            this.title = from.name.lowercase()
            this.types = from.getTypes().map { it.name }
        }

}