package com.arkhamusserver.arkhamus.model.dataaccess.redis

import org.springframework.stereotype.Component

@Component
class GameRelatedIdSource {

    companion object {
        const val DELIMITER = "::"
    }

    fun getId(gameId: Long, objectId: Long) =
        "${gameId}$DELIMITER${objectId}"

    fun getId(gameId: String, objectId: Long) =
        "$gameId$DELIMITER${objectId}"

}