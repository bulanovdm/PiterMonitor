package com.bulanovdm.pitermonitor

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.Properties

@Configuration
class Configurations(val mailProperties: MailProperties) {

    @Bean
    fun mailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "smtp.mailgun.org"
        mailSender.port = 587
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
}


@ConfigurationProperties(prefix = "mail.conf")
class MailProperties {
    lateinit var username: String
    lateinit var password: String
    lateinit var recipient: String
}