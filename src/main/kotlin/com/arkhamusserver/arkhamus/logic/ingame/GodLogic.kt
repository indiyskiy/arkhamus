package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.logic.ingame.item.GodToCorkResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.RecipesSource
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.view.dto.ingame.GodDto
import com.arkhamusserver.arkhamus.view.dto.ingame.GodWithCorksDto
import com.arkhamusserver.arkhamus.view.maker.ingame.GodToGodDtoMaker
import com.arkhamusserver.arkhamus.view.maker.ingame.GodWithCorksDtoMaker
import org.springframework.stereotype.Component

@Component
class GodLogic(
    private val godDtoMaker: GodToGodDtoMaker,
    private val godToCorkResolver: GodToCorkResolver,
    private val godWithCorksDtoMaker: GodWithCorksDtoMaker,
    private val recipesSource: RecipesSource,
) {
    fun listAllGods(): List<GodDto> =
        godDtoMaker.convert(God.values().toList())

    fun getGodsWithCorks(): List<GodWithCorksDto> =
        godWithCorksDtoMaker.convert(
            God.values().map {
                val item = godToCorkResolver.resolve(it)
                val recipe = recipesSource.getAllRecipes().first { it.item.id == item.id }
                GodWithCorksDtoMaker.Data(
                    god = it,
                    item = item,
                    recipe = recipe
                )
            }
        )

}