package com.bulanovdm.pitermonitor.service

import com.bulanovdm.pitermonitor.client.ShortenClient
import com.bulanovdm.pitermonitor.repo.BookService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Service
class LinkUpdater(
    private val bookService: BookService,
    private val shortenClient: ShortenClient
) {

    private val log = LoggerFactory.getLogger(javaClass)
    val bookCHM = ConcurrentHashMap<String, String>(1024, 0.95f)

    @Scheduled(initialDelay = 0, fixedRate = 180, timeUnit = TimeUnit.MINUTES)
    fun updateLinks() {
        log.info("Link updater started at ${LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)}")

        for (i in 1 until 7) {
            val doc: Document =
                Jsoup.connect("https://www.piter.com/collection/kompyutery-i-internet?only_available=true&order=&page=$i&page_size=100&q=")
                    .get()
            val products: Elements = doc.select(".products-list > * > a")
            for (product in products) {
                val title = product.attr("title")
                if (title.isNotEmpty()) {
                    bookCHM.computeIfAbsent(title) { "https://www.piter.com" + product.attr("href") }
                }
            }
        }

        log.info("Link updater holds ${bookCHM.size} books")
    }

    //@Scheduled(initialDelay = 1, fixedRate = 3600, timeUnit = TimeUnit.MINUTES)
    fun updateShortLinks() {
        //TODO("wip")
        log.info("Short links updater started at ${LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)}")
    }
}