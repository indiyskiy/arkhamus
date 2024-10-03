package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.view.dto.ingame.GodWithCorksDto
import org.springframework.stereotype.Component

@Component
class GodWithCorksDtoMaker(
    private val godToGodDtoMaker: GodToGodDtoMaker,
    private val itemInformationDtoMaker: ItemInformationDtoMaker,
    private val recipeDtoMaker: RecipeToRecipeDtoMaker
) {
    fun convert(dataList: List<Data>): List<GodWithCorksDto> =
        dataList.map {
            convert(it)
        }

    fun convert(dataItem: Data): GodWithCorksDto =
        GodWithCorksDto().apply {
            this.god = godToGodDtoMaker.convert(dataItem.god)
            this.cork = itemInformationDtoMaker.convert(dataItem.item)
            this.recipeDto = recipeDtoMaker.convert(dataItem.recipe)
        }

    data class Data(
        var god: God,
        var item: Item,
        var recipe: Recipe
    )
}