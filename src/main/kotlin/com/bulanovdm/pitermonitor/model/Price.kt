package com.bulanovdm.pitermonitor.model

import com.github.guepardoapps.kulid.ULID
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "price")
class Price(

    @Column(name = "variation")
    var variation: String,

    @Column(name = "price")
    var price: String
) {

    @Id
    @Column(name = "id")
    var id: String = ULID.random()

    override fun toString(): String {
        return "Цена: Вариант='$variation', стоимость='$price'"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Price

        if (variation != other.variation) return false
        if (price != other.price) return false

        return true
    }

    override fun hashCode(): Int {
        var result = variation.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }


}