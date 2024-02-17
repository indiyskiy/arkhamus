package com.arkhamusserver.arkhamus.globalutils

import java.util.*

fun String.capitalizeIfFirstCharIsLowercase() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}