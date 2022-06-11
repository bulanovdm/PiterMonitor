package com.bulanovdm.pitermonitor

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BookController(private val booksRepository: BooksRepository) {

    @GetMapping("/books")
    fun getAllBooks() : ResponseEntity<List<Book>> {
        return ResponseEntity.ok(booksRepository.findAll().toList())
    }

    @GetMapping("/discount")
    fun getAllDiscountBooks() : ResponseEntity<List<Book>> {
        return ResponseEntity.ok(booksRepository.findAll().filter { it.variants.any { v -> v.variantName == "Дисконт"} }.toList())
    }

    @GetMapping("/")
    fun get() : ResponseEntity<String> {
        return ResponseEntity.ok("ok")
    }
}