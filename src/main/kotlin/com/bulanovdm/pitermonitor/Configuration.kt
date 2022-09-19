package com.bulanovdm.pitermonitor

import io.lettuce.core.RedisURI
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.Properties


@Configuration
class Configurations(val mailProperties: MailProperties, val redisProperties: RedisProperties) {

    @Bean
    fun mailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = mailProperties.host
        mailSender.port = mailProperties.port.toInt()
        mailSender.username = mailProperties.username
        mailSender.password = mailProperties.password

        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.socketFactory.port"] = "587"
        props["mail.debug"] = "true"
        return mailSender
    }

    @Bean
    @Profile("dev")
    fun redisConnectionFactoryDev(): LettuceConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
        redisStandaloneConfiguration.password = RedisPassword.of(redisProperties.password)
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    @Profile("prod")
    fun redisConnectionFactoryProd(): LettuceConnectionFactory {
        val redisURI = RedisURI.create(redisProperties.url)
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(redisURI.host, redisURI.port)
        redisStandaloneConfiguration.database = redisURI.database
        redisStandaloneConfiguration.password = RedisPassword.of(redisURI.password)
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun redisTemplate(lettuceConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(lettuceConnectionFactory)
        return template
    }
}

@ConfigurationProperties(prefix = "mail.conf")
class MailProperties {
    lateinit var username: String
    lateinit var password: String
    lateinit var recipient: String
    lateinit var host: String
    lateinit var port: String
}