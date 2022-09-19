package com.bulanovdm.pitermonitor

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


@TestConfiguration
class TestRedisConfiguration(redisProperties: RedisProperties) {
    //embedded
    private val redisServer: RedisServer = RedisServer(redisProperties.port)

    @PostConstruct
    fun postConstruct() {
        redisServer.start()
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
    }
}