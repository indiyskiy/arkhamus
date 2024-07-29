package com.arkhamusserver.arkhamus.globalutils

import com.google.gson.Gson

fun Any.toJson(): String =
    Gson().toJson(this) + "\r\n"