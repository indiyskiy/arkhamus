package com.arkhamusserver.arkhamus.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class SchedulerConfig {
    @Bean
    fun taskScheduler(): ThreadPoolTaskScheduler {
        val cultpritsScheduler = ThreadPoolTaskScheduler().apply {
            this.poolSize = 20
            this.setThreadNamePrefix("cultprits-scheduled-task-pool-")
            this.setWaitForTasksToCompleteOnShutdown(false)
        }
        return cultpritsScheduler
    }
}