package ru.my.test

import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.my.test.entity.Author
import ru.my.test.entity.Book
import ru.my.test.service.AuthorRepository
import ru.my.test.service.BookRepository

@Component
class ModelHelper {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    companion object {
        private val faker = Faker()
    }


    @Transactional
    fun createBook(
        name: String = faker.book().title(),
        authors: List<Author> = emptyList()
    ): Book {
        val book = Book(name = name, authors = authors)
        bookRepository.save(book)
        return book
    }

    @Transactional
    fun createAuthor(
        name: String = faker.book().author(),
        books: List<Book> = emptyList()
    ): Author {
        val author = Author(name = name, books = books)
        authorRepository.save(author)
        return author
    }

}