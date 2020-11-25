package ru.my.test

import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.my.test.entity.*
import ru.my.test.service.AuthorRepository
import ru.my.test.service.BookRepository
import ru.my.test.service.ContactRepository
import ru.my.test.service.ReviewRepository
import org.springframework.transaction.annotation.Transactional

@Component
class ModelHelper {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    @Autowired
    private lateinit var contactRepository: ContactRepository

    // TODO: удалить faker так как нет гарантии что он возвращает уникальные данные. Так же усложняет код
    private val faker = Faker()

    fun createBook(
        name: String = faker.book().title(),
        authors: List<Author> = emptyList()
    ): Book {
        val book = Book(name = name, authors = authors)
        bookRepository.save(book)
        return book
    }

    fun createAuthor(
        name: String = faker.book().author(),
        books: List<Book> = emptyList()
    ): Author {
        val author = Author(name = name, books = books)
        authorRepository.save(author)
        return author
    }

    fun createReview(
        book: Book,
        rating: BookRating = BookRating.NORMAL,
        text: String = "",
    ): Review {
        val review = Review(text = text, book = book, rating = rating)
        reviewRepository.save(review)
        return review
    }

    fun createContact(
        author: Author = Author(name = "Author"),
        phone: String = faker.phoneNumber().phoneNumber(),
        email: String = faker.internet().emailAddress(),
    ): Contact {
        val contact = Contact(phone = phone, email = email, author = author)
        author.contact = contact
        authorRepository.save(author)
        return contact
    }

}