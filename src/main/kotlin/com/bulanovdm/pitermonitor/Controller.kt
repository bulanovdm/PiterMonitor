package com.bulanovdm.pitermonitor

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BookController(private val crawlService: CrawlService) {

    @GetMapping("/books")
    fun getAllBooks() : ResponseEntity<List<Book>> {
        return ResponseEntity.ok(crawlService.bookList.toList())
    }

    @GetMapping("/")
    fun get() : ResponseEntity<String> {
        return ResponseEntity.ok("ok")
    }
}