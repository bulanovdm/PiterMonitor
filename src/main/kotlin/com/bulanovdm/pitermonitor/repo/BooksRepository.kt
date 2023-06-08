package com.bulanovdm.pitermonitor.repo

import com.bulanovdm.pitermonitor.model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BooksRepository : JpaRepository<Book, String>