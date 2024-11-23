package com.arkhamusserver.arkhamus.config

import com.arkhamusserver.arkhamus.logic.UserStateInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val userStateInterceptor: UserStateInterceptor
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(userStateInterceptor)
            .addPathPatterns("/**")
    }
}