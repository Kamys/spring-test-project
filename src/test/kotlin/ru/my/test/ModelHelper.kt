package ru.my.test

import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.my.test.entity.Author
import ru.my.test.entity.Book
import ru.my.test.service.AuthorRepository
import ru.my.test.service.BookRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
class ModelHelper {

    @Autowired private lateinit var bookRepository: BookRepository
    @Autowired private lateinit var authorRepository: AuthorRepository

    private val faker = Faker()

    fun createBook(name: String = faker.book().title()): Book {
        val book = Book(name = name)
        bookRepository.save(book)
        return book
    }

    fun generateBirthday() : OffsetDateTime {
        return faker.date().birthday().toInstant().atOffset(ZoneOffset.UTC);
    }

    fun createAuthor(
        name: String = faker.book().author()
    ): Author {
        val author = Author(name = name)
        authorRepository.save(author)
        return author
    }

}