package com.bulanovdm.pitermonitor.model

import jakarta.persistence.*


@Entity
@Table(name = "book")
open class Book(

    @Id
    @Column(name = "title")
    var title: String,

    @Column(name = "link")
    var link: String,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "book_title")
    var prices: MutableList<Price> = mutableListOf()

) {
    override fun toString(): String {
        return "Книга: Заголовок: '$title', Ссылка: '$link', Цены: $prices"
    }
}