package com.bulanovdm.pitermonitor.service

import com.bulanovdm.pitermonitor.model.Book
import com.bulanovdm.pitermonitor.model.Price
import com.bulanovdm.pitermonitor.repo.BookService
import com.bulanovdm.pitermonitor.telegram.TelegramDiscountSender
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit


@Service
class CrawlService(
    private val linkUpdater: LinkUpdater,
    private val bookService: BookService,
    private val discountSender: TelegramDiscountSender,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(initialDelay = 1, fixedRate = 3600, timeUnit = TimeUnit.MINUTES)
    fun findDiscount() {
        val watch = StopWatch("schedule").also { it.start() }
        val bookToSend = CopyOnWriteArrayList<Book>()

        for (kv in linkUpdater.bookCHM) {
            val getBookByLinkDef = CompletableFuture.supplyAsync { Jsoup.connect(kv.value).get() }
            val oldBookDef = CompletableFuture.supplyAsync { bookService.findByTitle(kv.key) }

            val getBookByLink = getBookByLinkDef.get()
            val oldBook = oldBookDef.get()

            val variants = getBookByLink.select("div.grid-4.m-grid-12.s-grid-12.product-variants > *")
            val updatedBook = Book(kv.key, kv.value, mutableListOf())

            for (variant in variants) {
                val variantTitle = variant.getElementsByClass("variant-title")
                val variantPrice: Elements = variant.getElementsByClass("right grid-6 price color")
                val currentParsedPrice = Price(
                    variation = variantTitle.eachText().firstOrNull() ?: "Нет в продаже",
                    price = variantPrice.eachText().firstOrNull() ?: "Отсутсвует"
                )
                updatedBook.prices.add(currentParsedPrice)
            }

            if (oldBook?.prices != updatedBook.prices && updatedBook.prices.any { it.variation.contentEquals("Дисконт") }) {
                log.debug("Book added to send: {}", updatedBook)
                bookToSend.add(updatedBook)
            }

            if (updatedBook != oldBook) {
                log.info("Book updated after change: {}.\n Was: {}", updatedBook, oldBook)
                bookService.save(updatedBook)
            }
        }

        watch.stop().also { log.info("Parsing time result:\n {}", watch.prettyPrint()) }

        sendUpdates(bookToSend)
    }

    private fun sendUpdates(bookToSend: MutableList<Book>) {
        if (bookToSend.isNotEmpty()) {
            val batches = bookToSend.indices.groupBy { it / 20 }.map { entry -> entry.value.map(bookToSend::get) }
            batches.forEachIndexed { i, books ->
                val collectBooksToString = books.joinToString(
                    transform = { "${it.title}: ${it.link}\nЦена: ${it.discounts().first().price}" },
                    separator = "\n---\n"
                )

                discountSender.sendUpdates("Новые скидки! (${i + 1}/${batches.size})\n\n" + collectBooksToString)
            }
        }
        bookToSend.clear()
    }
}
