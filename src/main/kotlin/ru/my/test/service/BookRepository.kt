package ru.my.test.service

import javassist.NotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import ru.my.test.entity.Book
import ru.my.test.model.BookEditRequest

interface BookRepository : JpaRepository<Book, Int> {
/*    fun findOneByName(name: String) : Book*/


/*    fun findOrException(id: Int): Book {
        return this.findById(id).orElseThrow {
            NotFoundException("Failed find book with id=$id")
        }
    }*/
}

fun BookRepository.findOrException(id: Int): Book {
    return this.findById(id).orElseThrow {
        NotFoundException("Failed find book with id=$id")
    }
}