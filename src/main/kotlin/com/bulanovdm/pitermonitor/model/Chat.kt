package com.bulanovdm.pitermonitor.model

import com.github.guepardoapps.kulid.ULID
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "chat")
class Chat(

    @Column(name = "chat_id", nullable = false)
    var chatId: Long,

    @Column(name = "subscribed", nullable = false)
    var subscribed: Int = 0,
) {

    @Id
    @Column(name = "id")
    var id: String = ULID.random()
}