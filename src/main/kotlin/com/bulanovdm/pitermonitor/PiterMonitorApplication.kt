package com.bulanovdm.pitermonitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(MailProperties::class, RedisProperties::class)
class PiterMonitorApplication

fun main(args: Array<String>) {
    runApplication<PiterMonitorApplication>(*args)
}