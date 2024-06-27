package com.bulanovdm.pitermonitor

import com.bulanovdm.pitermonitor.model.Book
import com.bulanovdm.pitermonitor.model.Price
import com.bulanovdm.pitermonitor.repo.ChatService
import com.bulanovdm.pitermonitor.telegram.TelegramDiscountSender
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class TelegramServiceIntegrationTest {

    @Autowired
    private lateinit var telegramDiscountSender: TelegramDiscountSender

    @Autowired
    private lateinit var chatService: ChatService

    @Value("\${telegram.test-chat-id}")
    private lateinit var chatId: String

    @Test
    fun shouldSendMessage() {
        chatService.addChatSubscription(chatId.toLong())
        val books = listOf(Book("Name", "https://example.com", mutableListOf(Price(variation = "Дисконт", price = "some price"))))
        val collectBooksToString = books.joinToString(
            transform = { "${it.title}: ${it.link}\nЦена: ${it.discounts().first().price}" },
            separator = "\n --------- \n"
        )
        telegramDiscountSender.sendUpdates(collectBooksToString)
    }
}
