package com.arkhamusserver.arkhamus.view.validator

class ArkhamusValidationException(
    message: String,
    val relatedEntity: String
) : Exception(message)