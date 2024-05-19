package com.arkhamusserver.arkhamus.config.auth

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer

class MyCustomizer() : org.springframework.security.config.Customizer<ExceptionHandlingConfigurer<HttpSecurity>> {
    override fun customize(t: ExceptionHandlingConfigurer<HttpSecurity>) {
        t.accessDeniedHandler(CustomAccessDeniedHandler())
    }

}