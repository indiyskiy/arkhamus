package com.arkhamusserver.arkhamus.config

import com.arkhamusserver.arkhamus.logic.user.UserStateInterceptor
import com.arkhamusserver.arkhamus.util.logging.RequestResponseLoggingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val userStateInterceptor: UserStateInterceptor,
    private val requestResponseLoggingInterceptor: RequestResponseLoggingInterceptor
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        // Add request/response logging interceptor first to capture all requests
        registry.addInterceptor(requestResponseLoggingInterceptor)
            .addPathPatterns("/**")

        // Add user state interceptor
        registry.addInterceptor(userStateInterceptor)
            .addPathPatterns("/**")
    }
}
