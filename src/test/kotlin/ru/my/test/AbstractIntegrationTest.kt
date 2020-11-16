package ru.my.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import ru.my.test.entity.BaseEntity
import java.nio.charset.StandardCharsets

@SpringBootTest(classes = [Application::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(classes = [DataLoader::class])
abstract class AbstractIntegrationTest {
    @Autowired
    protected lateinit var mvc: MockMvc

    @Autowired
    protected lateinit var modelHelper: ModelHelper

    @Autowired
    lateinit var objectMapper: ObjectMapper

    protected val faker = Faker()

    final inline fun <reified T> MvcResult.asObject(): T {
        this.response.characterEncoding =
            StandardCharsets.UTF_8.name() // For correct parsing of cyrillic symbols in tests
        return this.response.contentAsString.asObject()
    }

    final inline fun <reified T> String.asObject(): T {
        return objectMapper.readValue(this)
    }

    fun Any.asJson(): String = objectMapper.writeValueAsString(this)

    @Throws(NullPointerException::class)
    fun <T> Iterable<T>.findOrException(predicate: (T) -> Boolean): T {
        return this.find(predicate) ?: throw NullPointerException("Failed find element")
    }

    fun MockMvc.get(
        url: String,
    ): ResultActions {
        return this.perform(MockMvcRequestBuilders.get(url))
    }

    fun MockMvc.post(
        url: String,
        content: String = "",
    ): ResultActions {
        val mockHttpRequest = MockMvcRequestBuilders
            .post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)
        return this.perform(mockHttpRequest)
    }

    fun MockMvc.put(
        url: String,
        content: String = "",
    ): ResultActions {
        val mockHttpRequest = MockMvcRequestBuilders
            .put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)
        return this.perform(mockHttpRequest)
    }

    fun MockMvc.delete(
        url: String,
    ): ResultActions {
        val mockHttpRequest = MockMvcRequestBuilders
            .delete(url)
            .contentType(MediaType.APPLICATION_JSON)
        return this.perform(mockHttpRequest)
    }
}