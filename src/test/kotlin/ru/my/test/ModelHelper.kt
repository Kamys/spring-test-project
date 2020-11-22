package ru.my.test

import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.my.test.entity.*
import ru.my.test.service.AuthorRepository
import ru.my.test.service.BookRepository
import ru.my.test.service.ReviewRepository
import ru.my.test.service.UserRepository

@Component
class ModelHelper {

    @Autowired
    private lateinit var bookRepository: BookRepository
    @Autowired
    private lateinit var authorRepository: AuthorRepository
    @Autowired
    private lateinit var reviewRepository: ReviewRepository
    @Autowired
    private lateinit var userRepository: UserRepository

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
        descriptionOfWrittenStyle: String = "",
        books: List<Book> = emptyList()
    ): Author {
        val user = this.createUser()

        val author = Author(
            descriptionOfWrittenStyle = descriptionOfWrittenStyle,
            books = books,
            user = user,
        )
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

    fun createUser(
        name: String = "User name"
    ): User {
        val user = User(name = name)
        userRepository.save(user)
        return user
    }

}