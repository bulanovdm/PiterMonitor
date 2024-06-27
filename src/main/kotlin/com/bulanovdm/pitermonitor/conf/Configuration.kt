package com.bulanovdm.pitermonitor.conf

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.time.Duration


@ConfigurationProperties(prefix = "telegram.bot")
class TelegramProperties {
    lateinit var name: String
    lateinit var token: String
}

@Configuration
class Configuration(
    private val telegramProperties: TelegramProperties
) {

    @Bean
    fun telegramClient(): TelegramClient = OkHttpTelegramClient(telegramProperties.token)

    @Bean
    fun bookCache(): CaffeineCache {
        return CaffeineCache(
            "books_cache",
            Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofDays(30))
                .maximumSize(1024)
                .build()
        )
    }

    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
        return restTemplate
    }
}