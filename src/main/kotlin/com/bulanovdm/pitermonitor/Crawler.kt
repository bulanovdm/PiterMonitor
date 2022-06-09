package com.bulanovdm.pitermonitor

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit


@Service
class CrawlService(private val bookMailService: BookMailService) : ApplicationListener<ContextRefreshedEvent> {
    private val log = LoggerFactory.getLogger(javaClass)
    val bookCHM = ConcurrentHashMap<String, String>(512);
    val bookList = CopyOnWriteArrayList<Book>()
    val bookToSend = mutableListOf<Book>()

    @Scheduled(initialDelay = 10, fixedRate = 60, timeUnit = TimeUnit.MINUTES)
    fun crawlAndSendUpdates() {
        populate()
        sendBookUpdates()
    }

    fun sendBookUpdates() {
        if (bookToSend.isNotEmpty()) {bookMailService.sendChangedBooks(bookToSend)}
        log.info("Mail ready. Books to send: {}", bookToSend.toString())
        bookToSend.clear()
    }

    fun populate() {
        log.info("Start Crawler at ${LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)}")

        for (i in 1 until 12) {
            val doc: Document =
                Jsoup.connect("https://www.piter.com/collection/diskont?only_available=true&order=&page=${i}&page_size=100&q=").get()
            val products: Elements = doc.select(".products-list > * > a")
            for (product in products) {
                bookCHM[product.attr("title")] = "https://www.piter.com" + product.attr("href")
            }
        }

        for (kv in bookCHM) {
            val getBookByLink: Document = Jsoup.connect(kv.value).get()
            val variants: Elements = getBookByLink.select("div.grid-4.m-grid-12.s-grid-12.product-variants > *")
            val book = Book(kv.key, kv.value, mutableListOf())

            if (bookList.none { it.name == book.name} ) {
                bookList.add(book)
            }

            for (variant in variants) {
                val varTitle = variant.getElementsByClass("variant-title")
                val varPrice: Elements = variant.getElementsByClass("right grid-6 price color")
                val currentParsedVariant = Variant(
                    varTitle.eachText().firstOrNull() ?: "Нет в продаже",
                    varPrice.eachText().firstOrNull() ?: "Отсутсвует"
                )

                if (!book.variants.contains(currentParsedVariant)) {
                    book.variants.add(currentParsedVariant)
                    bookToSend.add(book)
                }
            }
        }
        log.info("Books in memory: {}", bookList.size)
    }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        populate()
    }
}

data class Book(val name: String, val link: String, val variants: MutableList<Variant>)
data class Variant(val variantName: String, val variantPrice: String)