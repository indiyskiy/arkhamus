package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.view.dto.ingame.ClassInGameDto
import org.springframework.stereotype.Component

@Component
class ClassInGameDtoMaker {
    fun convert(values: Array<ClassInGame>): List<ClassInGameDto> {
        return values.map { convert(it) }
    }

    fun convert(value: ClassInGame): ClassInGameDto {
        return ClassInGameDto(
            id = value.id,
            name = value.name,
            value = value.name.lowercase(),
            roleType = value.roleType
        )
    }
}