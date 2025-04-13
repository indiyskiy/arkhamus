package com.arkhamusserver.arkhamus.gameplayQuality.items.roles

import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.view.validator.utils.assertTrue
import org.junit.jupiter.api.Test

class RolesAndClassesTest {
    @Test
    fun allClassesHaveDistinctIds() {
        val classesMap = ClassInGame.values().groupBy { it.id }
        classesMap.forEach { (id, classes) ->
            assertTrue(
                classes.size == 1,
                "The following classes have same id: ${classes.joinToString { it.name }} - ${id}",
                relatedObject = "ClassInGame"
            )
        }
    }
}