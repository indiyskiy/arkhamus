package com.arkhamusserver.arkhamus.logic.exception

class ArkhamusServerRequestException(
    message: String,
    val relatedEntity: String
) : Exception(message)