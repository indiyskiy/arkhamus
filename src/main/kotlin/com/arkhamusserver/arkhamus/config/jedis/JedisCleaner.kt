package com.arkhamusserver.arkhamus.config.jedis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.ContainerRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameUserRedisRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
@Component
class JedisCleaner(
    private val userRedisRepository: GameUserRedisRepository,
    private val containerRedisRepository: ContainerRedisRepository,
    private val gameRedisRepository: GameRedisRepository,
) {
        @PostConstruct
        fun cleanAll() {
            userRedisRepository.deleteAll()
            containerRedisRepository.deleteAll()
            gameRedisRepository.deleteAll()
        }

}