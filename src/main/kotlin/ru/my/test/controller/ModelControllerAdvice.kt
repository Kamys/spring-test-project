package ru.my.test.controller

import ru.my.test.model.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.my.test.model.*


@RestControllerAdvice
class ModelControllerAdvice {

    @ExceptionHandler(value = [NotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFoundException(exception: NotFoundException): ApiError {
        return ApiError(
            title = "Ресурс не найден",
            detail = exception.message
        )
    }

    @ExceptionHandler(value = [BadRequest::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun notFoundException(exception: BadRequest): ApiError {
        return ApiError(
            title = "Ошибка запроса",
            detail = exception.message
        )
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun methodArgumentNotValidException(exception: MethodArgumentNotValidException): ApiError {
        val violations = fromBindingResult(exception.bindingResult)
        return ApiValidationError(
            detail = exception.javaClass.simpleName,
            violations = violations
        )
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun httpMessageNotReadableException(exception: HttpMessageNotReadableException): ApiError {
        return ApiError(
            title = ErrorMessages.JSON_NOT_VALID.text,
            detail = exception.message
        )
    }

    private fun fromBindingResult(bindingResult: BindingResult): List<Violation> {
        val errors = bindingResult.allErrors
        return errors
            .mapNotNull { error ->
                when (error) {
                    is FieldError -> Violation(error.field, error.defaultMessage ?: "")
                    is ObjectError -> Violation(error.objectName, error.defaultMessage ?: "")
                    else -> null
                }
            }
            .toList()
    }
}
