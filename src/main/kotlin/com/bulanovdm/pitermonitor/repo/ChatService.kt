package com.bulanovdm.pitermonitor.repo

import com.bulanovdm.pitermonitor.model.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Repository
interface ChatRepository : JpaRepository<Chat, String> {

    @Query("SELECT c.chatId FROM Chat c WHERE c.subscribed = 1")
    fun findActive(): List<Long>

    fun existsChatByChatId(id: Long): Boolean

    @Modifying
    @Query("UPDATE Chat c SET c.subscribed = 1 WHERE c.chatId = :chatId")
    fun enableSubscription(@Param("chatId") chatId: Long): Int

    @Modifying
    @Query("UPDATE Chat c SET c.subscribed = 0 WHERE c.chatId = :chatId")
    fun disableSubscription(@Param("chatId") chatId: Long): Int

}

@Service
@Transactional
class ChatService(
    private val chatRepository: ChatRepository
) {

    fun addChatSubscription(chatId: Long) {
        if (!chatRepository.existsChatByChatId(chatId)) {
            chatRepository.save(Chat(chatId = chatId, subscribed = 1))
        } else {
            chatRepository.enableSubscription(chatId)
        }
    }

    fun removeChat(chatId: Long) {
        if (!chatRepository.existsChatByChatId(chatId)) {
            chatRepository.save(Chat(chatId = chatId, subscribed = 0))
        } else {
            chatRepository.disableSubscription(chatId)
        }
    }

    fun findActive(): List<Long> {
        return chatRepository.findActive()
    }
}
