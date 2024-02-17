package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.globalutils.capitalizeIfFirstCharIsLowercase
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.view.dto.ingame.GodDto
import org.springframework.stereotype.Component

@Component
class GodToGodDtoMaker {
    fun convert(from: Collection<God>): List<GodDto> =
        from.map { convert(it) }

    fun convert(from: God): GodDto =
        GodDto().apply {
            this.id = from.getId()
            this.name = from
            this.title = from.name.lowercase().replace("_", " ")
                .capitalizeIfFirstCharIsLowercase()
            this.types = from.getTypes()
        }

}