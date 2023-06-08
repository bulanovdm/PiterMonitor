package com.bulanovdm.pitermonitor.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "price")
open class Price(

    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),

    @Column(name = "variation")
    var variation: String,

    @Column(name = "price")
    var price: String
) {

    override fun toString(): String {
        return "Цена: Вариант='$variation', стоимость='$price'"
    }
}