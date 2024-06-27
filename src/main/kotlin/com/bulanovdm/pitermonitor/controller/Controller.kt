package com.bulanovdm.pitermonitor.controller

import com.bulanovdm.pitermonitor.model.Book
import com.bulanovdm.pitermonitor.repo.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BookController(private val bookService: BookService) {

    @GetMapping("/books")
    fun getAllBooks(): ResponseEntity<List<Book>> {
        return ResponseEntity.ok(bookService.findAll().toList())
    }

    @GetMapping("/discount")
    fun getAllDiscountBooks(): ResponseEntity<List<Book>> {
        return ResponseEntity.ok(bookService.findAllDiscount().toList())
    }
}