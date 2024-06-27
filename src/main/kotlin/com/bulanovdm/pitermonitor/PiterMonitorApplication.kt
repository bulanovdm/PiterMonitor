package com.bulanovdm.pitermonitor

import com.bulanovdm.pitermonitor.conf.TelegramProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableJpaRepositories
@EnableCaching
@EnableConfigurationProperties(TelegramProperties::class)
@SpringBootApplication
@EnableFeignClients
class PiterMonitorApplication

fun main(args: Array<String>) {
    runApplication<PiterMonitorApplication>(*args)
}