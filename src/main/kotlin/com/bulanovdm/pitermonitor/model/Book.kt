package com.bulanovdm.pitermonitor.model

import com.github.guepardoapps.kulid.ULID
import jakarta.persistence.*

@Entity
@Table(name = "book")
class Book(

    @Column(name = "title")
    var title: String,

    @Column(name = "link")
    var link: String,

    @Column(name = "short_link")
    var shortLink: String?,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(name = "book_id")
    var prices: MutableList<Price> = mutableListOf()
) {

    @Id
    @Column(name = "id")
    var id: String = ULID.random()

    override fun toString(): String {
        return "Книга: '$title: $link', Цены: ${prices.joinToString(transform = { "${it.variation}=${it.price}" }, separator = ", ")}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        if (title != other.title) return false
        if (link != other.link) return false
        if (prices != other.prices) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + link.hashCode()
        result = 31 * result + prices.hashCode()
        return result
    }

    fun discounts(): List<Price> {
        return prices.filter { it.variation == "Дисконт" }
    }

}