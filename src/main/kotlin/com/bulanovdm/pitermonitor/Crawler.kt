package com.bulanovdm.pitermonitor

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.io.Serializable
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


@Service
class CrawlService(private val bookMailService: BookMailService, val booksRepository: BooksRepository) :
    ApplicationListener<ContextRefreshedEvent> {

    private val log = LoggerFactory.getLogger(javaClass)
    val bookCHM = ConcurrentHashMap<String, String>(1024);
    val bookToSend = mutableListOf<Book>()
    val bookToSendWas = mutableListOf<Book>()

    @Scheduled(initialDelay = 15, fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    fun readySendMail() {
        for (kv in bookCHM) {
            val getBookByLink: Document = Jsoup.connect(kv.value).get()
            val variants: Elements = getBookByLink.select("div.grid-4.m-grid-12.s-grid-12.product-variants > *")
            val oldBook = booksRepository.findById(kv.key).get()
            val changedBook = Book(kv.key, kv.value, mutableListOf())

            for (variant in variants) {
                val varTitle = variant.getElementsByClass("variant-title")
                val varPrice: Elements = variant.getElementsByClass("right grid-6 price color")
                val currentParsedVariant = Variant(
                    varTitle.eachText().firstOrNull() ?: "Нет в продаже",
                    varPrice.eachText().firstOrNull() ?: "Отсутсвует"
                )
                changedBook.variants.add(currentParsedVariant)

                if (!oldBook.variants.contains(currentParsedVariant)) {
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
            bookMailService.sendChangedBooks(bookToSend, bookToSendWas)
            bookToSend.clear()
        }
    }

    fun populate() {
        log.info("Start Crawler at ${LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)}")
        val findAllBooksInRedis = booksRepository.findAll()

        for (i in 1 until 6) {
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
                val currentParsedVariant = Variant(
                    varTitle.eachText().firstOrNull() ?: "Нет в продаже",
                    varPrice.eachText().firstOrNull() ?: "Отсутсвует"
                )

                if (!book.variants.contains(currentParsedVariant)) {
                    book.variants.add(currentParsedVariant)
                }

                if (findAllBooksInRedis.none { it.name == book.name && it.variants.contains(currentParsedVariant) }) {
                    log.info("Book saved: {}", book)
                    booksRepository.save(book)
                }
            }
        }
        log.info("Books in memory: {}", booksRepository.count())
    }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        populate()
    }
}

@RedisHash("Book")
data class Book(@Id val name: String, val link: String, val variants: MutableList<Variant>) : Serializable
data class Variant(val variantName: String, val variantPrice: String)

@Repository
interface BooksRepository : CrudRepository<Book, String>