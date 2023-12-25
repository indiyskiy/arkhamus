package com.arkhamusserver.arkhamus.view.validator.utils

fun assertTrue(expression: Boolean, message: String? = null) {
    if (!expression) {
        message?.let { throw IllegalStateException(message) } ?: throw IllegalStateException()
    }
}

fun assertNotNull(notNullable: Any?, message: String? = null) {
    if (notNullable == null) {
        message?.let { throw IllegalStateException(message) } ?: throw IllegalStateException()
    }
}