package com.bulanovdm.pitermonitor.telegram

import com.bulanovdm.pitermonitor.repo.ChatService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient


@Service
class TelegramDiscountSender(
    private val telegramClient: TelegramClient,
    private val chatService: ChatService
) : LongPollingSingleThreadUpdateConsumer {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun consume(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            when (update.message.text) {
                "/start" -> {
                    chatService.addChatSubscription(update.message.chatId)
                    sendMessageAsync(update.message.chatId, "Вы подписались на обновления")
                }

                "/stop" -> {
                    chatService.removeChat(update.message.chatId)
                    sendMessageAsync(update.message.chatId, "Вы отписались от обновлений")
                }

                else -> sendMessageAsync(update.message.chatId, "Бот поддерживает только команды /start и /stop")
            }
        }
    }

    fun sendUpdates(textToSend: String) {
        chatService.findActive().forEach { chat -> sendMessageAsync(chat, textToSend) }
    }

    private fun sendMessageAsync(chatId: Long, text: String) {
        val message = SendMessage.builder()
            .text(text)
            .chatId(chatId)
            .disableWebPagePreview(true)
            .build()

        runCatching {
            telegramClient.executeAsync(message)
        }.onFailure { log.error("Something went wrong while trying to send message to chat: $chatId", it) }
            .getOrNull()
    }

}