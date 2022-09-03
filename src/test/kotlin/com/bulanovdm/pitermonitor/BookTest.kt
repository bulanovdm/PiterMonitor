package com.bulanovdm.pitermonitor

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@ActiveProfiles("test")
@SpringBootTest(classes = [TestRedisConfiguration::class])
class UserRepositoryIntegrationTest {

    @Autowired
    private lateinit var bookRepository: BooksRepository

    @Test
    fun shouldSaveBook_toRedis() {
        val book = Book("Name", "https://example.com", mutableListOf(Variant("vName", "some price")))
        val saved: Book = bookRepository.save(book)
        assertNotNull(saved)
    }
}