package com.bulanovdm.pitermonitor.repo

import com.bulanovdm.pitermonitor.model.Book
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Repository
interface BookRepository : JpaRepository<Book, String> {

    @Query("SELECT b FROM Book b JOIN FETCH b.prices WHERE b.title = :title")
    fun findBooksByTitleLike(title: String): List<Book>
}

@Service
@Transactional
class BookService(private val repository: BookRepository) {

    @CachePut(key = "#saved.title", cacheNames = ["books_cache"])
    fun save(saved: Book): Book {
        return repository.save(saved)
    }

    @Cacheable(key = "#title", cacheNames = ["books_cache"])
    fun findByTitle(title: String): Book? {
        return repository.findBooksByTitleLike(title).firstOrNull()
    }

    fun findAll(): Iterable<Book> {
        return repository.findAll()
    }

    fun findAllDiscount(): Iterable<Book> {
        return repository.findAll().filter { it.prices.any { v -> v.variation == "Дисконт" } }
    }
}
