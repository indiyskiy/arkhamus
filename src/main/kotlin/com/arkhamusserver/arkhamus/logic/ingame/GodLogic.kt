package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.logic.dto.ingame.GodDto
import com.arkhamusserver.arkhamus.logic.dto.ingame.GodWithCorksDto
import com.arkhamusserver.arkhamus.logic.ingame.item.GodToCorkResolver
import com.arkhamusserver.arkhamus.logic.maker.ingame.GodToGodDtoMaker
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import org.springframework.stereotype.Component

@Component
class GodLogic(
    private val godDtoMaker: GodToGodDtoMaker,
    private val godToCorkResolver: GodToCorkResolver
) {
    fun listAllGods(): List<GodDto> =
        godDtoMaker.convert(God.values().toList())

    fun getGodsWithCorks(): List<GodWithCorksDto> =
        God.values().map {
            GodWithCorksDto().apply {
                god = it.name
                cork = godToCorkResolver.resolve(it).name
            }
        }

}