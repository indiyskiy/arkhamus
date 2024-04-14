package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.view.dto.ingame.ClassInGameDto
import com.arkhamusserver.arkhamus.view.maker.ingame.ClassInGameDtoMaker
import org.springframework.stereotype.Component

@Component
class ClassInGameLogic(private val classInGameDtoMaker: ClassInGameDtoMaker) {

    fun listAllClasses(): List<ClassInGameDto> {
        return classInGameDtoMaker.convert(ClassInGame.values())
    }

}