package com.bulanovdm.pitermonitor.telegram

import com.bulanovdm.pitermonitor.conf.TelegramProperties
import org.springframework.stereotype.Component
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot

@Component
class TelegramDiscountBot(
    private val telegramProperties: TelegramProperties,
    private val telegramDiscountSender: TelegramDiscountSender
) : SpringLongPollingBot {

    override fun getBotToken(): String = telegramProperties.token

    override fun getUpdatesConsumer(): LongPollingUpdateConsumer = telegramDiscountSender
}