package ru.my.test

import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.my.test.entity.Book
import ru.my.test.service.BookRepository

@Component
class ModelHelper {

    @Autowired private lateinit var bookRepository: BookRepository

    private val faker = Faker()

    fun createBook(name: String = faker.book().title()): Book {
        val book = Book(name = name)
        bookRepository.save(book)
        return book
    }

}