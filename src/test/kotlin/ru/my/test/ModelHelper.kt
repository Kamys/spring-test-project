package ru.my.test

import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.my.test.entity.Book
import ru.my.test.service.BookRepository

@Component
class ModelHelper(
    @Autowired private val bookRepository: BookRepository,
) {

    private val faker = Faker()

    fun createBook(): Book {
        val book = Book(name = faker.book().title())
        bookRepository.save(book)
        return book
    }

}