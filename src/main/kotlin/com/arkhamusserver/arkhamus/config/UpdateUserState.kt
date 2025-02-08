package com.arkhamusserver.arkhamus.config

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UpdateUserState(val value: CultpritsUserState)
