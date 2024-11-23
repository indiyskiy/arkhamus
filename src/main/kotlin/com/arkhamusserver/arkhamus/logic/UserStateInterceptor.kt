package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.config.UpdateUserState
import com.arkhamusserver.arkhamus.model.dataaccess.UserStatusService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class UserStateInterceptor(
    private val userStatusService: UserStatusService,
    private val currentUserService: CurrentUserService,
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val method = handler.method

            val updateUserState = method.getAnnotation(UpdateUserState::class.java)
            if (updateUserState != null) {
                val state = updateUserState.value
                val userId = currentUserService.getCurrentUserAccount().id
                userId?.let { userStatusService.updateUserStatus(it, state) }
            }
        }
        return true
    }
}