package com.bulanovdm.pitermonitor

import com.bulanovdm.pitermonitor.model.Book
import com.bulanovdm.pitermonitor.model.Price
import com.bulanovdm.pitermonitor.repo.BookRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@ActiveProfiles("dev")
@SpringBootTest
class UserRepositoryIntegrationTest {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Test
    fun shouldSaveBook() {
        val book = Book("Name", "https://example.com", "", mutableListOf(Price(variation = "vName", price = "some price")))
        val saved: Book = bookRepository.save(book)
        assertNotNull(saved)
    }
}