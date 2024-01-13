package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.view.dto.ingame.GodWithCorksDto
import org.springframework.stereotype.Component

@Component
class GodWithCorksDtoMaker(
    private val godToGodDtoMaker: GodToGodDtoMaker,
    private val itemInformationDtoMaker: ItemInformationDtoMaker
) {
    fun convert(dataList: List<Data>): List<GodWithCorksDto> =
        dataList.map {
            convert(it)
        }

    fun convert(dataItem: Data): GodWithCorksDto =
        GodWithCorksDto().apply {
            this.god = godToGodDtoMaker.convert(dataItem.god)
            this.cork = itemInformationDtoMaker.convert(dataItem.item)
        }

    data class Data(
        var god: God,
        var item: Item
    )
}