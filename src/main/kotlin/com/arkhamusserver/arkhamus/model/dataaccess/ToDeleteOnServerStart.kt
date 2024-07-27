package com.arkhamusserver.arkhamus.model.dataaccess

interface ToDeleteOnServerStart<T> {
    fun deleteAll()
}