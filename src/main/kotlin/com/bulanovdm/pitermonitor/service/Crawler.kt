package com.bulanovdm.pitermonitor.service

import com.bulanovdm.pitermonitor.model.Book
import com.bulanovdm.pitermonitor.model.Price
import com.bulanovdm.pitermonitor.repo.BooksRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Profiles
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.TimeUnit


@Service
@Transactional
class CrawlService(private val bookMailService: BookMailService, val booksRepository: BooksRepository) {

    private val log = LoggerFactory.getLogger(javaClass)
    val bookCHM = ConcurrentHashMap<String, String>(1024, 0.95f)
    val bookToSend = CopyOnWriteArraySet<Book>()
    val bookToSendWas = CopyOnWriteArraySet<Book>()
    val discountSet = CopyOnWriteArraySet<Book>()

    @Scheduled(initialDelay = 30, fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    fun readySendMail() {
        for (kv in bookCHM) {
            val getBookByLink: Document = Jsoup.connect(kv.value).get()
            val variants: Elements = getBookByLink.select("div.grid-4.m-grid-12.s-grid-12.product-variants > *")
            val oldBook = booksRepository.findById(kv.key).get()
            val changedBook = Book(kv.key, kv.value, mutableListOf())

            for (variant in variants) {
                val varTitle = variant.getElementsByClass("variant-title")
                val varPrice: Elements = variant.getElementsByClass("right grid-6 price color")
                val currentParsedPrice = Price(
                    variation = varTitle.eachText().firstOrNull() ?: "Нет в продаже",
                    price = varPrice.eachText().firstOrNull() ?: "Отсутсвует"
                )
                changedBook.prices.add(currentParsedPrice)

                if (!oldBook.prices.contains(currentParsedPrice) && changedBook.prices.any { it.variation == "Дисконт" }
                ) {
                    bookToSendWas.add(oldBook)
                    bookToSend.add(changedBook)
                }
            }
            if (oldBook != changedBook) {
                log.info("Book updated after mail: {}", changedBook)
                booksRepository.save(changedBook)
            }
        }

        if (bookToSend.isNotEmpty()) {
            log.info("Mail ready. Books to send: {}", bookToSend.toString())
            discountAll()
            bookMailService.sendChangedBooks(bookToSend, bookToSendWas, discountSet)
            bookToSendWas.clear()
            bookToSend.clear()
            discountSet.clear()
        }
    }

    private fun discountAll() {
        val findAll = booksRepository.findAll()
        for (book in booksRepository.findAll()) {
            for (price in book.prices) {
                if (price.variation == "Дисконт" && price.price != "Отсутсвует" && price.price.isNotBlank()) {
                    discountSet.add(book)
                }
            }
        }
    }

    fun populate() {
        log.info("Start Crawler at ${LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)}")
        val findAllBooks = booksRepository.findAll()

        for (i in 1 until 7) {
            val doc: Document =
                Jsoup.connect("https://www.piter.com/collection/kompyutery-i-internet?only_available=true&order=&page=$i&page_size=100&q=")
                    .get()
            val products: Elements = doc.select(".products-list > * > a")
            for (product in products) {
                if (product.attr("title").isNotEmpty()) {
                    bookCHM[product.attr("title")] = "https://www.piter.com" + product.attr("href")
                }
            }
        }

        for (kv in bookCHM) {
            val getBookByLink: Document = Jsoup.connect(kv.value).get()
            val variants: Elements = getBookByLink.select("div.grid-4.m-grid-12.s-grid-12.product-variants > *")
            val book = Book(kv.key, kv.value, mutableListOf())

            for (variant in variants) {
                val varTitle = variant.getElementsByClass("variant-title")
                val varPrice: Elements = variant.getElementsByClass("right grid-6 price color")
                val currentParsedPrice = Price(
                    variation = varTitle.eachText().firstOrNull() ?: "Нет в продаже",
                    price = varPrice.eachText().firstOrNull() ?: "Отсутсвует"
                )

                if (!book.prices.contains(currentParsedPrice)) {
                    book.prices.add(currentParsedPrice)
                }

                if (findAllBooks.none { it.title == book.title && it.prices.contains(currentParsedPrice) }) {
                    log.info("Book saved: {}", book)
                    booksRepository.save(book)
                }
            }
        }
        log.info("Books in memory: {}", booksRepository.count())
    }

    @EventListener
    fun onApplicationEvent(event: ApplicationReadyEvent) {
        if (event.applicationContext.environment.acceptsProfiles(Profiles.of("prod"))) {
            populate()
        }
    }
}

