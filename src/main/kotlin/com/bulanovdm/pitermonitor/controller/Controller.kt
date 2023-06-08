package com.bulanovdm.pitermonitor.controller

import com.bulanovdm.pitermonitor.model.Book
import com.bulanovdm.pitermonitor.repo.BooksRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BookController(private val booksRepository: BooksRepository) {

    @GetMapping("/books")
    fun getAllBooks(): ResponseEntity<List<Book>> {
        return ResponseEntity.ok(booksRepository.findAll().toList())
    }

    @GetMapping("/discount")
    fun getAllDiscountBooks(): ResponseEntity<List<Book>> {
        return ResponseEntity.ok(booksRepository.findAll().filter { it.prices.any { v -> v.variation == "Дисконт" } }.toList())
    }

    @GetMapping("/")
    fun get(): ResponseEntity<String> {
        return ResponseEntity.ok("ok")
    }
}