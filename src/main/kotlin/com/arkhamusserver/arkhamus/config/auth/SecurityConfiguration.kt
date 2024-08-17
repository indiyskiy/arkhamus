package  com.arkhamusserver.arkhamus.config.auth

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider
) {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain {
        val customizer = MyCustomizer()
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(AntPathRequestMatcher("/")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/public/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/steam/**")).permitAll()
                    //web page content part
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/css/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/js/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/images/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/fonts/**")).permitAll()
                    //admin part
                    .requestMatchers(AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                    .anyRequest().authenticated()
            }.exceptionHandling(customizer)

//                it
//                    .requestMatchers("/auth", "/error")
//                    .permitAll()
//                    .requestMatchers("/api/user**")
//                    .hasRole("ADMIN")
//                    .anyRequest()
//                    .fullyAuthenticated()

            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

}