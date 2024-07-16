package com.arkhamusserver.arkhamus.view.validator.utils

import com.arkhamusserver.arkhamus.view.validator.ArkhamusValidationException

fun assertTrue(expression: Boolean, message: String, relatedObject: String) {
    if (!expression) {
        throw ArkhamusValidationException(message, relatedObject)
    }
}

fun assertEquals(value1: Number, value2: Number, message: String, relatedObject: String) {
    if (value1 != value2) {
        throw ArkhamusValidationException(message, relatedObject)
    }
}

fun assertNotNull(notNullable: Any?, message: String, relatedObject: String) {
    if (notNullable == null) {
        throw ArkhamusValidationException(message, relatedObject)
    }
}